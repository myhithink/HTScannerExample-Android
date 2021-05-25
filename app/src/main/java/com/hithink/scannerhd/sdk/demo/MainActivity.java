package com.hithink.scannerhd.sdk.demo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hithink.scannerhd.sdk.HTScanner;
import com.hithink.scannerhd.sdk.HTScannerCommonConfig;
import com.hithink.scannerhd.sdk.HTScannerProject;
import com.hithink.scannerhd.sdk.callback.HTScannerExportCallback;
import com.hithink.scannerhd.sdk.callback.HTScannerProjectCallback;
import com.hithink.scannerhd.sdk.constant.HTScannerExportType;
import com.hithink.scannerhd.sdk.custom.HTScannerConfigId;
import com.hithink.scannerhd.sdk.custom.HTScannerPageId;
import com.hithink.scannerhd.selectpiclib.ImageLoadEngine;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView mTipTextView;
    private EditText mInputLicenseEditText;
    private Button mInitScannerButton;
    private Button mCheckLicenseValidButton;
    private RadioButton mExportTypeImageRadioButton;
    private RadioButton mExportTypePdfRadioButton;
    private RadioButton mrExportTypeThumbnailRadioButton;
    private Button mStartScanButton;
    private EditText mExportNameEditText;
    private Button mExportButton;
    private Button mJumpToSettingPageButton;

    private Switch mMultiPageModeSwitch;
    private Switch mShowMutiPageModeSwitch;
    private Switch mAutoCaptureModeSwitch;
    private Switch mShowAutoCaptureModeShowSwitch;
    private Switch mShowCapturePickerSwitch;
    private Switch mShowOcrSwitch;

    private HTScanner mHTScanner;
    private HTScannerProject mHtScannerProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initListener();

    }

    private void initView(){
        mTipTextView = findViewById(R.id.tv_tip);
        mInputLicenseEditText = findViewById(R.id.et_license);
        mInitScannerButton = findViewById(R.id.btn_init_scanner);
        mCheckLicenseValidButton = findViewById(R.id.btn_check_license_valid);
        mExportTypeImageRadioButton = findViewById(R.id.rb_image);
        mExportTypePdfRadioButton = findViewById(R.id.rb_pdf);
        mrExportTypeThumbnailRadioButton = findViewById(R.id.rb_thumbnail);
        mStartScanButton = findViewById(R.id.btn_start_scan);
        mExportNameEditText = findViewById(R.id.et_export_name);
        mExportButton = findViewById(R.id.btn_export);
        mJumpToSettingPageButton = findViewById(R.id.btn_jump_to_setting_page);

        mMultiPageModeSwitch = findViewById(R.id.switch_multi_page);
        mShowMutiPageModeSwitch = findViewById(R.id.switch_show_multi_page_mode);
        mAutoCaptureModeSwitch = findViewById(R.id.switch_auto_capture_mode);
        mShowAutoCaptureModeShowSwitch = findViewById(R.id.switch_show_auto_capture_mode);
        mShowCapturePickerSwitch = findViewById(R.id.switch_show_capture_picker);
        mShowOcrSwitch = findViewById(R.id.switch_ocr);
    }

    private void initData(){
        mHTScanner = HTScanner.instance();
        customImageLoadEngine();
    }

    private void customImageLoadEngine(){
        ImageLoadEngine imageLoadEngine = new ImageLoadEngine() {
            @Override
            public void loadPicture(ImageView imageView, String imageUrl, int width, int height, Drawable placeDrawable, boolean withCache) {

            }

            @Override
            public void clearMemory() {

            }

            @Override
            public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {

            }

            @Override
            public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {

            }

            @Override
            public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {

            }

            @Override
            public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {

            }

            @Override
            public boolean supportAnimatedGif() {
                return false;
            }
        };
        // Host app can custom imageLoader, Scanner SDK will use GlideImageLoadEngine if not set
//        mHTScanner.setImageLoadEngine(imageLoadEngine);
    }

    private HTScannerCommonConfig createHTScannerCommonConfig(){
        return new HTScannerCommonConfig.Builder()
                // set maximum number of pictures be selected in normal mode
                .setMaxCaptureImageCountInCommonMode(100)
                // whether to add pictures serially
                .setAddImageInSerialMode(false)
                // whether check support scan quality
                .setCheckSupportScanQuality(false)
                // set default color filter type . e.g. HTScannerCommonConfig.Builder.ENHANCE
                .setDefaultColorFilterType(HTScannerCommonConfig.Builder.ENHANCE)
                .build();
    }

    private void initListener(){
        mInitScannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int code = mHTScanner.initScanner(MainActivity.this.getApplication(),
                        mInputLicenseEditText.getText().toString().trim(), createHTScannerCommonConfig());
                if(code == 0){
                    updateTip("init Scanner success");
                }else{
                    updateTip("init Scanner failed code=" + code);
                }
            }
        });

        mCheckLicenseValidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mHTScanner.isLicenseValid()){
                    updateTip("sdk is license valid");
                }else{
                    updateTip("sdk license is not valid");
                }
            }
        });

        mStartScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int code = mHTScanner.startScan(MainActivity.this, new HTScannerProjectCallback() {
                    @Override
                    public void onScanResult(boolean finished, HTScannerProject htScannerProject) {
                        if(finished){
                            mHtScannerProject = htScannerProject;
                            String title = null;
                            int pageCount = 0;
                            long createTime = 0L;
                            if(null != mHtScannerProject){
                                title = mHtScannerProject.getTitle();
                                pageCount = mHtScannerProject.getPageCount();
                                createTime = mHtScannerProject.getCreateTime();
                            }
                            updateTip(String.format("scan success title:%1$s, pageCount:%2$d, createTime:%3$s",
                                    title, pageCount, formatTime(createTime)));
                        }else{
                            updateTip("user cancelled scan");
                        }
                    }
                });
                if(code == 0){
                    updateTip("start scan success");
                }else{
                    updateTip("start scan failed code=" + code);
                }
            }
        });

        mExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null == mHtScannerProject){
                    updateTip("need scan first");
                    return;
                }
                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "scannerDemo");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String exportRelativeDir = "scannerDemo";
                int exportType = HTScannerExportType.HT_SCANNER_EXPORT_TYPE_IMAGE;
                if(mExportTypePdfRadioButton.isChecked()){
                    exportType = HTScannerExportType.HT_SCANNER_EXPORT_TYPE_PDF;
                }else if(mrExportTypeThumbnailRadioButton.isChecked()){
                    exportType = HTScannerExportType.HT_SCANNER_EXPORT_TYPE_THUMBNAIL;
                }
                String exportName = mExportNameEditText.getText().toString().trim();
                int code = mHtScannerProject.export(exportRelativeDir, exportName, exportType, new HTScannerExportCallback() {
                    @Override
                    public void onExportSuccess(List<String> list) {
                        String msg;
                        if(list != null && list.size() != 0){
                            msg = "export success list=" + list.toString();
                        }else{
                            msg = "export failed list is null>error!";
                        }
                        updateTip(msg);
                    }

                    @Override
                    public void onExportFailed(int errorCode) {
                        updateTip("export failed errorCode=" + errorCode);
                    }
                });
                if(code == 0){
                    updateTip("export success");
                }else{
                    updateTip("export failed code=" + code);
                }
            }
        });

        mJumpToSettingPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int code = mHTScanner.displaySettingsPage(MainActivity.this);
                if(code == 0){
                    updateTip("jump to setting page success");
                }else{
                    updateTip("jump to setting page failed code=" + code);
                }
            }
        });

        mMultiPageModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int code = mHTScanner.setUIConfig(HTScannerPageId.HXScannerCapturePage,
                        HTScannerConfigId.HTScannerCapturePageSetMultiPages, isChecked);
                if(code == 0){
                    updateTip("switch single/batch mode success");
                }else{
                    updateTip("switch single/batch mode failed code=" + code);
                }
            }
        });

        mShowMutiPageModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int code = mHTScanner.setUIConfig(HTScannerPageId.HXScannerCapturePage,
                        HTScannerConfigId.HTScannerCapturePageShowMultiPages, isChecked);
                if(code == 0){
                    updateTip("show/hide \"batch\" switch success");
                }else{
                    updateTip("show/hide \"batch\" switch failed code=" + code);
                }
            }
        });

        mAutoCaptureModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int code = mHTScanner.setUIConfig(HTScannerPageId.HXScannerCapturePage,
                        HTScannerConfigId.HTScannerCapturePageSetAutoCapture, isChecked);
                if(code == 0){
                    updateTip("switch manual/auto mode success");
                }else{
                    updateTip("switch manual/auto mode failed code=" + code);
                }
            }
        });

        mShowAutoCaptureModeShowSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int code = mHTScanner.setUIConfig(HTScannerPageId.HXScannerCapturePage,
                        HTScannerConfigId.HTScannerCapturePageShowAutoCapture, isChecked);
                if(code == 0){
                    updateTip("show/hide \"auto\" switch success");
                }else{
                    updateTip("show/hide \"auto\" switch failed code=" + code);
                }
            }
        });

        mShowCapturePickerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int code = mHTScanner.setUIConfig(HTScannerPageId.HXScannerCapturePage,
                        HTScannerConfigId.HTScannerCapturePageHideCapturePicker, isChecked);
                if(code == 0){
                    updateTip("show/hide capture picker success");
                }else{
                    updateTip("show/hide capture picker failed code=" + code);
                }
            }
        });
        mShowOcrSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int code = mHTScanner.setUIConfig(HTScannerPageId.HTScannerPreviewPage,
                        HTScannerConfigId.HTScannerPreviewPageHideOcrButton, isChecked);
                if(code == 0){
                    updateTip("show/hide OCR button success");
                }else{
                    updateTip("show/hide OCR button failed code=" + code);
                }
            }
        });
    }

    private void updateTip(String msg){
        mTipTextView.setText(msg);
    }

    private SimpleDateFormat mSimpleDateFormat;
    private String formatTime(long time){
        if(null == mSimpleDateFormat){
            mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        }
        return mSimpleDateFormat.format(time * 1000);
    }
}
