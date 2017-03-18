package com.zhan.haoqi.bottle.ui.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.data.Callback;
import com.zhan.haoqi.bottle.data.User;
import com.zhan.haoqi.bottle.data.UserManager;
import com.zhan.haoqi.bottle.http.BaseSubscriber;
import com.zhan.haoqi.bottle.http.HttpError;
import com.zhan.haoqi.bottle.http.HttpHelper;
import com.zhan.haoqi.bottle.http.RequestParam;
import com.zhan.haoqi.bottle.util.AppUtils;
import com.zhan.haoqi.bottle.util.ImageShowUtil;
import com.zhan.haoqi.bottle.util.MaterialDialogUtil;
import com.zhan.haoqi.bottle.util.MediaManager;
import com.zhan.haoqi.bottle.util.To;
import com.zhan.haoqi.bottle.util.UriUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zah on 2016/11/24.
 */

public class UserInfoEditActivity extends Activity {
    private static final int REQUEST_CROP_IMAGE = 3;
    @BindView(R.id.avatar_view)
    ImageView avatarView;
    @BindView(R.id.nick_name)
    EditText nick_name;
    @BindView(R.id.signature)
    EditText signature;
    @BindView(R.id.gender_txt)
    TextView gender_txt;
    private String uploadimgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        ButterKnife.bind(this);
        ((TextView) findViewById(R.id.title)).setText("用户信息");
        setUserInfo();
    }


    private void setUserInfo() {
        User user = UserManager.getInstance().getUser();
        if (!TextUtils.isEmpty(user.avatar)) {
            if (user.avatar.startsWith("http")) {
                ImageShowUtil.display(this, user.avatar, avatarView, 0, false);
            } else {
                ImageShowUtil.display(this, HttpHelper.getAvatarProFix(user.avatar), avatarView, R.mipmap.user_editavatar, false);
            }
        }
        nick_name.setText(user.nick_name);
        gender_txt.setText(user.gender == 0 ? "女" : "男");

        if (!TextUtils.isEmpty(user.signature)) {
            signature.setText(user.signature);
        }
    }

    @OnClick({R.id.back, R.id.avatar, R.id.gender, R.id.save_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.avatar:
                showSelectImgDialog();
                break;
            case R.id.gender:
                MaterialDialogUtil.showDialogTip(UserInfoEditActivity.this, "性别无法修改");
                break;
            case R.id.save_submit:
                submitUserInfo();
                break;
        }
    }


    private PopupWindow submit;

    private void submitUserInfo() {

//
        String nickNameStr = nick_name.getText().toString().trim();
        if (TextUtils.isEmpty(nickNameStr)) {
            To.show("昵称不能为空");
            return;
        }
        if (nickNameStr.length() > 16) {
            To.show("昵称不能超过16位");
            return;
        }

        submit = To.showPop(this, R.id.save_submit, "正在修改...");

        File avatarFile = null;
        if (!TextUtils.isEmpty(uploadimgPath)) {
            avatarFile = new File(uploadimgPath);
        }
        RequestParam param = new RequestParam();
        param.put("nick_name", nickNameStr);
        param.put("signature", signature.getText().toString().trim());
        if (avatarFile != null && avatarFile.exists()) {
            param.put("avatar", avatarFile);
        }
        UserManager.getInstance().modifyUserInfo(param, new BaseSubscriber<JSONObject>() {
            @Override
            public void onNext(JSONObject jsonObject) {
                UserManager.getInstance().praseAndSave(jsonObject);
                To.dismiss(submit);
                To.show("保存成功！");
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                To.dismiss(submit);
                To.show("保存失败！");
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
                    File file = MediaManager.getPhotoFromCamera(UserInfoEditActivity.this);
                    if (file != null) {
                        uploadimgPath = file.getAbsolutePath();
                    }
                } else if (i == 1) {
                    MediaManager.getPhotoFromAlbum(UserInfoEditActivity.this);
                }
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MediaManager.REQUEST_CAMERA && resultCode == RESULT_OK) {
            if (!TextUtils.isEmpty(uploadimgPath)) {
                // findViewById(R.id.image_container).setVisibility(View.VISIBLE);
                ImageShowUtil.display("file://" + uploadimgPath, avatarView, true);
                chooseSmalPic();
            }
        } else if (requestCode == MediaManager.REQUEST_GALLERY && resultCode == RESULT_OK) {
            String path = UriUtil.getPath(this, data.getData());
            if (!TextUtils.isEmpty(path)) {
                uploadimgPath = path;
                //findViewById(R.id.image_container).setVisibility(View.VISIBLE);
                ImageShowUtil.display("file://" + uploadimgPath, avatarView, true);
                chooseSmalPic();
            }
        } else if (requestCode == REQUEST_CROP_IMAGE && resultCode == RESULT_OK) {
            ImageShowUtil.display(this, "file://" + uploadimgPath, avatarView, false);
        }
    }

    private void chooseSmalPic() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(new File(uploadimgPath)), "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别


        uploadimgPath = MediaManager.createTmpFile(this).getAbsolutePath();
        Uri uritempFile = Uri.parse("file://" + "/" + uploadimgPath);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, REQUEST_CROP_IMAGE);
    }


    @OnClick(R.id.save_submit)
    public void onClick() {
    }
}
