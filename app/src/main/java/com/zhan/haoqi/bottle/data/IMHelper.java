package com.zhan.haoqi.bottle.data;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMMessage.Type;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.controller.EaseUI.EaseEmojiconInfoProvider;
import com.hyphenate.easeui.controller.EaseUI.EaseSettingsProvider;
import com.hyphenate.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.model.EaseNotifier.EaseNotificationInfoProvider;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;
import com.im.Constant;
import com.im.PreferenceManager;
import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.ui.ChatActivity;
import com.zhan.haoqi.bottle.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import haoqi.emoji.util.EmojiUtils;

public class IMHelper {
    /**
     * data sync listener
     */
    public interface DataSyncListener {
        /**
         * sync complete
         *
         * @param success true：data sync successful，false: failed to sync data
         */
        void onSyncComplete(boolean success);
    }

    protected static final String TAG = "IMHelper";

    private EaseUI easeUI;

    /**
     * EMEventListener
     */
    protected EMMessageListener messageListener = null;
    private static IMHelper instance = null;
    private Context appContext;
    private ArrayList<String> notNotifyIds = new ArrayList<>();

    private IMHelper() {
    }

    public synchronized static IMHelper getInstance() {
        if (instance == null) {
            instance = new IMHelper();
        }
        return instance;
    }

    /**
     * init helper
     *
     * @param context application context
     */
    public void init(Context context) {
        EMOptions options = initChatOptions();
        //use default options if options is null
        if (EaseUI.getInstance().init(context, options)) {
            appContext = context;

            //debug mode, you'd better set it to false, if you want release your App officially.
            EMClient.getInstance().setDebugMode(true);
            //get easeui instance
            easeUI = EaseUI.getInstance();
            //to set user's profile and avatar
            setEaseUIProviders();
            //initialize preference manager
            PreferenceManager.init(context);
            //initialize profile manager
            setGlobalListeners();
        }
    }


    private EMOptions initChatOptions() {
        Log.d(TAG, "init HuanXin Options");

        EMOptions options = new EMOptions();
        // set if accept the invitation automatically
        options.setAcceptInvitationAlways(false);
        // set if you need read ack
        options.setRequireAck(true);
        // set if you need delivery ack
        options.setRequireDeliveryAck(false);


        //其他推送
        //you need apply & set your own id if you want to use google cloud messaging.
//        options.setGCMNumber("324169311137");
//        //you need apply & set your own id if you want to use Mi push notification
//        options.setMipushConfig("2882303761517426801", "5381742660801");
//        //you need apply & set your own id if you want to use Huawei push notification
//        options.setHuaweiPushAppId("10492024");

        return options;
    }

    protected void setEaseUIProviders() {
        // set profile provider if you want easeUI to handle avatar and nickname
        easeUI.setUserProfileProvider(new EaseUserProfileProvider() {

            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });

        //set options
        easeUI.setSettingsProvider(new EaseSettingsProvider() {

                                       @Override
                                       public boolean isSpeakerOpened() {
                                           return true;
                                       }

                                       @Override
                                       public boolean isMsgVibrateAllowed(EMMessage message) {
                                           return true;
                                       }

                                       @Override
                                       public boolean isMsgSoundAllowed(EMMessage message) {
                                           return true;
                                       }

                                       @Override
                                       public boolean isMsgNotifyAllowed(EMMessage message) {
                                           if (message == null) {
                                               return true;
                                           }
                                           String chatUsename = null;
                                           // get user or group id which was blocked to show message notifications
                                           if (message.getChatType() == ChatType.Chat) {
                                               chatUsename = message.getFrom();
                                           }

                                           if (!notNotifyIds.contains(chatUsename)) {
                                               return true;
                                           } else {
                                               return false;
                                           }
                                       }
                                   }

        );
        //set emoji icon provider
        easeUI.setEmojiconInfoProvider(new EaseEmojiconInfoProvider() {
                                           @Override
                                           public EaseEmojicon getEmojiconInfo(String emojiconIdentityCode) {
                                               EmojiUtils.getInstance().getEmojiIcons();
                                               return null;
                                           }

                                           @Override
                                           public Map<String, Object> getTextEmojiconMapping() {
                                               return null;
                                           }
                                       }

        );

        //set notification options, will use default if you don't set it
        easeUI.getNotifier().

                setNotificationInfoProvider(new EaseNotificationInfoProvider() {

                                                @Override
                                                public String getTitle(EMMessage message) {
                                                    //you can update title here
                                                    return null;
                                                }

                                                @Override
                                                public int getSmallIcon(EMMessage message) {
                                                    //you can update icon here
                                                    return 0;
                                                }

                                                @Override
                                                public String getDisplayedText(EMMessage message) {
                                                    // be used on notification bar, different text according the message type.
                                                    String ticker = EaseCommonUtils.getMessageDigest(message, appContext);
                                                    if (message.getType() == Type.TXT) {
                                                        ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                                                    }
                                                    EaseUser user = getUserInfo(message.getFrom());
                                                    if (user != null) {
                                                        if (EaseAtMessageHelper.get().isAtMeMsg(message)) {
                                                            return String.format(appContext.getString(R.string.at_your_in_group), user.getNick());
                                                        }
                                                        return user.getNick() + ": " + ticker;
                                                    } else {
                                                        if (EaseAtMessageHelper.get().isAtMeMsg(message)) {
                                                            return String.format(appContext.getString(R.string.at_your_in_group), message.getFrom());
                                                        }
                                                        return message.getFrom() + ": " + ticker;
                                                    }
                                                }

                                                @Override
                                                public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                                                    // here you can customize the text.
                                                    // return fromUsersNum + "contacts send " + messageNum + "messages to you";
                                                    return null;
                                                }

                                                @Override
                                                public Intent getLaunchIntent(EMMessage message) {
                                                    // you can set what activity you want display when user click the notification
                                                    Intent intent = new Intent(appContext, ChatActivity.class);
                                                    // open calling activity if there is call
                                                    ChatType chatType = message.getChatType();
                                                    if (chatType == ChatType.Chat) { // single chat message
                                                        intent.putExtra("userId", message.getFrom());
                                                        intent.putExtra("chatType", Constant.CHATTYPE_SINGLE);
                                                    } else { // group chat message
//                                                            // message.getTo() is the group id
//                                                            intent.putExtra("userId", message.getTo());
//                                                            if (chatType == ChatType.GroupChat) {
//                                                                intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
//                                                            } else {
//                                                                intent.putExtra("chatType", Constant.CHATTYPE_CHATROOM);
//                                                            }
                                                    }
                                                    return intent;
                                                }
                                            }

                );
    }

    EMConnectionListener connectionListener;

    /**
     * set global listener
     */
    protected void setGlobalListeners() {
        // create the global connection listener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                EMLog.d("global listener", "onDisconnect" + error);
                if (error == EMError.USER_REMOVED) {
                    onUserException(Constant.ACCOUNT_REMOVED);
                } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    onUserException(Constant.ACCOUNT_CONFLICT);
                } else if (error == EMError.SERVER_SERVICE_RESTRICTED) {
                    onUserException(Constant.ACCOUNT_FORBIDDEN);
                }
            }

            @Override
            public void onConnected() {
            }
        };
        //register connection listener
        EMClient.getInstance().addConnectionListener(connectionListener);
        //register group and contact event listener
//        registerGroupAndContactListener();
        //register message event listener
        registerMessageListener();
    }

    void showToast(final String message) {
        Message msg = Message.obtain(handler, 0, message);
        handler.sendMessage(msg);
    }

    protected android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            String str = (String) msg.obj;
            Toast.makeText(appContext, str, Toast.LENGTH_LONG).show();
        }
    };

    /**
     * user met some exception: conflict, removed or forbidden
     */
    protected void onUserException(String exception) {
        EMLog.e(TAG, "onUserException: " + exception);
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(exception, true);
        appContext.startActivity(intent);
    }

    private EaseUser getUserInfo(String username) {
        // To get instance of EaseUser, here we get it from the user list in memory
        // You'd better cache it if you get it from your server
        EaseUser user = null;
        if (username.equals(EMClient.getInstance().getCurrentUser())) {
            user = new EaseUser(username);
            user.setNickname(UserManager.getInstance().getUser().nick_name);
            user.setAvatar(UserManager.getInstance().getUser().avatar);
            return user;
        }
        User tempUser = UserManager.getInstance().getUserMap().get(username);
        user = new EaseUser(username);
        if (tempUser != null) {
            user.setNickname(tempUser.nick_name);
            user.setAvatar(tempUser.avatar);
        }
        //            EaseCommonUtils.setUserInitialLetter(user); 首字母
        return user;
    }

    /**
     * Global listener
     * If this event already handled by an activity, you don't need handle it again
     * activityList.size() <= 0 means all activities already in background or not in Activity Stack
     */
    protected void registerMessageListener() {
        messageListener = new EMMessageListener() {
            private BroadcastReceiver broadCastReceiver = null;

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
                    // in background, do not refresh UI, notify it in notification bar
                    if (!easeUI.hasForegroundActivies()) {
                        getNotifier().onNewMsg(message);
                    }
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    EMLog.d(TAG, "receive command message");
                    //get message body
                    EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                    final String action = cmdMsgBody.action();//获取自定义action
                    //red packet code : 处理红包回执透传消息
//                    if (!easeUI.hasForegroundActivies()) {
//                        if (action.equals(RPConstant.REFRESH_GROUP_RED_PACKET_ACTION)) {
//                            RedPacketUtil.receiveRedPacketAckMessage(message);
//                            broadcastManager.sendBroadcast(new Intent(RPConstant.REFRESH_GROUP_RED_PACKET_ACTION));
//                        }
//                    }

                    if (action.equals("__Call_ReqP2P_ConferencePattern")) {
                        String title = message.getStringAttribute("em_apns_ext", "conference call");
                        Toast.makeText(appContext, title, Toast.LENGTH_LONG).show();
                    }
                    //end of red packet code
                    //获取扩展属性 此处省略
                    //maybe you need get extension of your message
                    //message.getStringAttribute("");
                    EMLog.d(TAG, String.format("Command：action:%s,message:%s", action, message.toString()));
                }
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                EMLog.d(TAG, "change:");
                EMLog.d(TAG, "change:" + change);
            }
        };

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    /**
     * if ever logged in
     *
     * @return
     */
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    /**
     * logout
     *
     * @param unbindDeviceToken whether you need unbind your device token
     * @param callback          callback
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
        Log.d(TAG, "logout: " + unbindDeviceToken);
        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "logout: onSuccess");
                reset();
                if (callback != null) {
                    callback.onSuccess();
                }

            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.d(TAG, "logout: onSuccess");
                reset();
                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }

    /**
     * get instance of EaseNotifier
     *
     * @return
     */
    public EaseNotifier getNotifier() {
        return easeUI.getNotifier();
    }

    synchronized void reset() {
//        DemoDBManager.getInstance().closeDB();
    }

    public void pushActivity(Activity activity) {
        easeUI.pushActivity(activity);
    }

    public void popActivity(Activity activity) {
        easeUI.popActivity(activity);
    }

}
