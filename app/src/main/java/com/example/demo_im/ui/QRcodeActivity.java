package com.example.demo_im.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo_im.R;
import com.example.demo_im.model.FriendProfile;
import com.google.zxing.WriterException;
import com.module.zxing.encode.CodeCreator;
import com.squareup.picasso.Picasso;
import com.tencent.TIMUserProfile;
import com.tencent.qcloud.presentation.presenter.FriendshipManagerPresenter;
import com.tencent.qcloud.presentation.viewfeatures.FriendInfoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class QRcodeActivity extends Activity implements FriendInfoView{
    private String TAG=QRcodeActivity.class.getSimpleName();
    private ImageView avatar;
    private TextView name;
    private ImageView qrCode;
    private TextView saveCode;
    private FriendshipManagerPresenter presenter;
    private Context context;
    private Bitmap bitmap;
    private String basePath= Environment.getExternalStorageDirectory().getPath();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_qrcode);
        avatar=(ImageView)findViewById(R.id.qrCodeAvatar);
        name=(TextView)findViewById(R.id.name);
        qrCode=(ImageView)findViewById(R.id.qrCode);
        saveCode=(TextView)findViewById(R.id.saveCode);
        saveCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBitmap();
            }
        });
        presenter=new FriendshipManagerPresenter(this);
        presenter.getMyProfile();
    }

    @Override
    public void showUserInfo(List<TIMUserProfile> users) {
        TIMUserProfile profile=users.get(0);
        FriendProfile friendProfile=new FriendProfile(profile);
        Picasso.with(context)
                .load(friendProfile.getAvatarUrl())
                .placeholder(friendProfile.getAvatarRes())
                .error(friendProfile.getAvatarRes())
                .centerCrop()
                .into(avatar);
        name.setText(friendProfile.getRemark());
        try {
            bitmap=CodeCreator.createQRCode(friendProfile.getIdentify());
            qrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void saveBitmap() {
        File f = new File(basePath+"/im/");
        Log.e(TAG,"file path="+f.getAbsolutePath());
        if (!f.exists()) {
            f.mkdirs();
        }
        File picture=new File(f.getPath(),"qrCode.png");
        if (picture.exists()) {
            picture.delete();
        }
        try {
            picture.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream out = new FileOutputStream(picture);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Log.e(TAG,"file size="+picture);
            out.flush();
            out.close();
            Toast.makeText(QRcodeActivity.this,"已保存到"+f.getAbsolutePath(),Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    picture.getAbsolutePath(), picture.getName(), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + picture.getPath())));

    }


}
