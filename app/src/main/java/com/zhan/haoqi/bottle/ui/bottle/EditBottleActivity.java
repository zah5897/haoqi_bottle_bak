package com.zhan.haoqi.bottle.ui.bottle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.data.UserManager;
import com.zhan.haoqi.bottle.data.type.BottleType;
import com.zhan.haoqi.bottle.http.BaseSubscriber;
import com.zhan.haoqi.bottle.http.BottleHttpManager;
import com.zhan.haoqi.bottle.ui.LoginActivity;
import com.zhan.haoqi.bottle.ui.MainActivity;
import com.zhan.haoqi.bottle.ui.image.ReviewSelectedImageActivity;
import com.zhan.haoqi.bottle.util.AppUtils;
import com.zhan.haoqi.bottle.util.ImageFactory;
import com.zhan.haoqi.bottle.util.ImageShowUtil;
import com.zhan.haoqi.bottle.util.MediaManager;
import com.zhan.haoqi.bottle.util.To;
import com.zhan.haoqi.bottle.util.UriUtil;
import com.zhan.haoqi.bottle.view.HQTypeCommonDialog;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import haoqi.emoji.main.EaseEmojicon;
import haoqi.emoji.ui.view.EmojiEditText;
import haoqi.emoji.ui.view.EmojiKeyboard;

/**
 * Created by zah on 2016/10/21.
 */
public class EditBottleActivity extends Activity implements View.OnClickListener, EmojiKeyboard.EventListener, TextWatcher {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.edit_bottle_face_container)
    EmojiKeyboard editBottleFaceContainer;
    @BindView(R.id.edit_bottle_input)
    EmojiEditText editBottleInput;
    @BindView(R.id.back)
    ImageView action_bar_left_text;
    @BindView(R.id.menu_right)
    TextView action_bar_right_text;
    @BindView(R.id.edit_bottle_content_img)
    ImageView contentImg;
    @BindView(R.id.type_secret)
    CheckBox typeSecret;
    @BindView(R.id.type_topic)
    CheckBox typeTopic;
    private String uploadimgPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bottle);
        ButterKnife.bind(this);
        editBottleFaceContainer.setEventListener(this);
        editBottleInput.setOnClickListener(this);
        editBottleInput.addTextChangedListener(this);
        editBottleInput.requestFocus();
        title.setText(getString(R.string.bottle_create));
        action_bar_left_text.setVisibility(View.VISIBLE);
        action_bar_left_text.setOnClickListener(this);
        contentImg.setOnClickListener(this);
        action_bar_right_text.setVisibility(View.VISIBLE);
        action_bar_right_text.setText("扔出去");
        findViewById(R.id.menu_right).setOnClickListener(this);
        findViewById(R.id.type_tip).setOnClickListener(this);
        setCheckListener();
    }


    private void setCheckListener() {
        typeSecret.setOnCheckedChangeListener(typeSecretCheckedListener);
        typeTopic.setOnCheckedChangeListener(typeTopicCheckedListener);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.edit_bottle_face) {
            editBottleFaceContainer.setVisibility(View.VISIBLE);
        } else {
            editBottleFaceContainer.setVisibility(View.GONE);
        }

        if (v.getId() != R.id.edit_bottle_input) {
            AppUtils.hideSoftInputIsShow(this, editBottleInput);
        } else {
            editBottleInput.requestFocus();
            AppUtils.showSoftInputIsShow(this, editBottleInput);
        }

        switch (v.getId()) {
            case R.id.edit_bottle_sel_img:
                showSelectImgDialog();
                break;
            case R.id.menu_right:
                if (!UserManager.getInstance().isLogin()) {
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    sendBottle();
                }
                break;
            case R.id.back:
                finish();
                break;
            case R.id.edit_bottle_content_img:
                Intent review = new Intent(this, ReviewSelectedImageActivity.class);
                review.putExtra("localUrl", uploadimgPath);
                startActivityForResult(review, MediaManager.REQUEST_REVIEW);
                break;
            case R.id.del_img:
                uploadimgPath = "";
                findViewById(R.id.image_container).setVisibility(View.GONE);
                break;
            case R.id.type_tip:
                new HQTypeCommonDialog(this).show();
                break;
        }
    }


    private File toRemove;

    private void sendBottle() {
        String content = editBottleInput.getText().toString();
        File file = null;
        if (TextUtils.isEmpty(uploadimgPath)) {
            if (content.length() < 6) {
                To.show("内容不能少于6个字");
                return;
            }
        } else {
            file = new File(uploadimgPath);
        }
        BottleType type = BottleType.BROADCAST;
        if (typeSecret.isChecked()) {
            type = BottleType.SECRET;
        } else if (typeTopic.isChecked()) {
            type = BottleType.TOPIC;
        } else {
            type = BottleType.BROADCAST;
        }
        File tempFile = null;
        if (file != null && file.exists()) {
            tempFile = MediaManager.createTmpFile(getApplication());
            ImageFactory.compress(file.getAbsolutePath(), tempFile.getAbsolutePath(), ImageFactory.IMG_BOTTLE_MAX_SIZE, 10);
        }
        if (tempFile == null || !tempFile.exists()) {
            tempFile = file;
        }
        toRemove = tempFile;
        BottleHttpManager.sendBottle(tempFile, content, type.ordinal(), new BaseSubscriber<JSONObject>() {
            @Override
            public void onCompleted() {
                Intent intent = new Intent(MainActivity.ACTION_THROW_BOTTLE);
                sendBroadcast(intent);
                finish();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(JSONObject jsonObject) {
                MediaManager.deleteFile(toRemove);
                onCompleted();
            }
        });
    }

    private void showSelectImgDialog() {
        ArrayList localArrayList = new ArrayList();
        localArrayList.add("拍照");
        localArrayList.add("相册");
        AppUtils.getDialogListActionSheet(this, localArrayList, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    File file = MediaManager.getPhotoFromCamera(EditBottleActivity.this);
                    if (file != null) {
                        uploadimgPath = file.getAbsolutePath();
                    }
                } else if (i == 1) {
                    MediaManager.getPhotoFromAlbum(EditBottleActivity.this);
                }
            }
        }).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MediaManager.REQUEST_CAMERA && resultCode == RESULT_OK) {
            if (!TextUtils.isEmpty(uploadimgPath)) {
                findViewById(R.id.image_container).setVisibility(View.VISIBLE);
                ImageShowUtil.display(this, "file://" + uploadimgPath, contentImg, false);
            }
        } else if (requestCode == MediaManager.REQUEST_GALLERY && resultCode == RESULT_OK) {
            String path = UriUtil.getPath(this, data.getData());
            if (!TextUtils.isEmpty(path)) {
                uploadimgPath = path;
                findViewById(R.id.image_container).setVisibility(View.VISIBLE);
                ImageShowUtil.display(this, "file://" + uploadimgPath, contentImg, false);
            }
        } else if (requestCode == MediaManager.REQUEST_REVIEW) {
            if (data != null) {
                boolean del = data.getBooleanExtra("delete", false);
                if (del) {
                    uploadimgPath = "";
                    findViewById(R.id.image_container).setVisibility(View.GONE);
                }
            }
        }

    }

    @Override
    public void onBackspace() {
        EmojiKeyboard.backspace(this.editBottleInput);
    }

    @Override
    public void onEmojiSelected(EaseEmojicon emojicon) {
        EmojiKeyboard.input(this.editBottleInput, emojicon);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    private CompoundButton.OnCheckedChangeListener typeTopicCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                typeSecret.setChecked(false);
            }
        }
    };
    private CompoundButton.OnCheckedChangeListener typeSecretCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                typeTopic.setChecked(false);
            }
        }
    };

}
