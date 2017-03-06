package com.zhan.haoqi.bottle.ui.user;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.data.AccountLoginListener;
import com.zhan.haoqi.bottle.data.User;
import com.zhan.haoqi.bottle.data.UserManager;
import com.zhan.haoqi.bottle.http.BaseSubscriber;
import com.zhan.haoqi.bottle.http.HttpError;
import com.zhan.haoqi.bottle.http.RequestParam;
import com.zhan.haoqi.bottle.util.AppUtils;
import com.zhan.haoqi.bottle.util.ImageShowUtil;
import com.zhan.haoqi.bottle.util.MaterialDialogUtil;
import com.zhan.haoqi.bottle.util.MediaManager;
import com.zhan.haoqi.bottle.util.To;
import com.zhan.haoqi.bottle.util.UriUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zah on 2016/11/24.
 */

public class RegistActivity extends Activity {


    private static final int REQUEST_CROP_IMAGE = 3;
    @BindView(R.id.action_bar_middle_text)
    TextView actionBarMiddleText;
    @BindView(R.id.user_avatar)
    ImageView userAvatar;
    @BindView(R.id.action_bar_right_text)
    TextView actionBarRightText;
    @BindView(R.id.birthday)
    TextView birthday;
    @BindView(R.id.nick_name)
    EditText nickName;
    @BindView(R.id.pwd)
    EditText pwd;
    @BindView(R.id.repwd)
    EditText repwd;

    private String uploadimgPath;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        user = new User();
        user.birthday = "1990-06-01";
        user.gender = -1;
        ButterKnife.bind(this);
        actionBarMiddleText.setText("注册");
        birthday.setText("生日(" + user.birthday + ")");
    }

    @OnClick({R.id.back, R.id.submit, R.id.user_avatar, R.id.birthday, R.id.female, R.id.male})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.submit:
                submitRegist();
                break;
            case R.id.user_avatar:
                showSelectImgDialog();
                break;
            case R.id.birthday:
                String[] date = (TextUtils.isEmpty(user.birthday) ? "1990-6-1" : user.birthday).split("-");
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String yy_mm_dd = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        birthday.setText(yy_mm_dd);
                        user.birthday = yy_mm_dd;
                    }
                }, Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2])).show();

                break;
            case R.id.male:
                findViewById(R.id.male).setBackgroundColor(getResources().getColor(R.color.app_color));
                findViewById(R.id.female).setBackgroundColor(getResources().getColor(R.color.white));
                user.gender = 1;
                MaterialDialogUtil.showDialogTip(this, "注意，注册成功后性别无法修改");
                break;
            case R.id.female:
                findViewById(R.id.female).setBackgroundColor(getResources().getColor(R.color.app_color));
                findViewById(R.id.male).setBackgroundColor(getResources().getColor(R.color.white));
                user.gender = 0;
                MaterialDialogUtil.showDialogTip(this, "注意，注册成功后性别无法修改");
                break;
        }
    }

    private PopupWindow submit;

    private void submitRegist() {

        String phone = getIntent().getStringExtra("phone");

        String nickNameStr = nickName.getText().toString().trim();
        if (TextUtils.isEmpty(nickNameStr)) {
            To.show("昵称不能为空");
            return;
        }
        if (nickNameStr.length() > 16) {
            To.show("昵称不能超过16位");
            return;
        }

        if (user.gender < 0) {
            To.show("请选择性别");
            return;
        }

        String pwdString = pwd.getText().toString();

        if (TextUtils.isEmpty(pwdString.trim())) {
            To.show("密码不能为空");
            return;
        }


        if (pwdString.trim().length() < 6) {
            To.show("密码太短");
            return;
        }

        String repwdStr = repwd.getText().toString();

        if (!pwdString.equals(repwdStr)) {
            To.show("两次输入的密码不一致");
            return;
        }

        submit = To.showPop(this, R.id.submit, "正在提交注册...");

        File avatarFile = null;
        if (!TextUtils.isEmpty(uploadimgPath)) {
            avatarFile = new File(uploadimgPath);
        }
        RequestParam param = new RequestParam();
        param.put("mobile", phone);
        param.put("password", pwdString);
        param.put("nick_name", nickNameStr);
        param.put("gender", user.gender);
        param.put("birthday", user.birthday);
        if (avatarFile != null && avatarFile.exists()) {
            param.put("avatar", avatarFile);
        }
        UserManager.getInstance().regist(param, new BaseSubscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        HttpError he = (HttpError) e;
                        To.dismiss(submit);
                    }

                    @Override
                    public void onNext(JSONObject o) {
                        To.dismiss(submit);
                        To.show("注册成功！");
                        setResult(RESULT_OK);
                        finish();
                        UserManager.getInstance().praseAndSave(o);
                        return;
                    }
                }

        );

    }


    private void showSelectImgDialog() {
        ArrayList localArrayList = new ArrayList();
        localArrayList.add("拍照");
        localArrayList.add("相册");
        AppUtils.getDialogListActionSheet(this, localArrayList, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    File file = MediaManager.getPhotoFromCamera(RegistActivity.this);
                    if (file != null) {
                        uploadimgPath = file.getAbsolutePath();
                    }
                } else if (i == 1) {
                    MediaManager.getPhotoFromAlbum(RegistActivity.this);
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
                //ImageShowUtil.display("file://" + uploadimgPath, userAvatar, true);
                chooseSmalPic();
            }
        } else if (requestCode == MediaManager.REQUEST_GALLERY && resultCode == RESULT_OK) {
            String path = UriUtil.getPath(this, data.getData());
            if (!TextUtils.isEmpty(path)) {
                uploadimgPath = path;
                //findViewById(R.id.image_container).setVisibility(View.VISIBLE);
                //ImageShowUtil.display("file://" + uploadimgPath, userAvatar, true);
                chooseSmalPic();
            }
        } else if (requestCode == REQUEST_CROP_IMAGE && resultCode == RESULT_OK) {
            ImageShowUtil.display(this, "file://" + uploadimgPath, userAvatar, false);
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


    @OnClick(R.id.birthday)
    public void onClick() {
    }
}
