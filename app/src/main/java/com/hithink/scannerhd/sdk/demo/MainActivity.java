package com.hithink.scannerhd.sdk.demo;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hithink.scannerhd.sdk.HTScanner;
import com.hithink.scannerhd.sdk.HTScannerProject;
import com.hithink.scannerhd.sdk.callback.HTScannerExportCallback;
import com.hithink.scannerhd.sdk.callback.HTScannerProjectCallback;
import com.hithink.scannerhd.sdk.constant.HTScannerExportType;
import com.hithink.scannerhd.sdk.custom.HTScannerConfigId;
import com.hithink.scannerhd.sdk.custom.HTScannerPageId;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView mTipTextView;
    private EditText mInputLicenseEditText;
    private Button mInitScannerButton;
    private Button mCheckLicenseValidButton;
    private RadioButton mExportTypeImageRadioButton;
    private RadioButton mExportTypePdfRadioButton;
    private Button mStartScanButton;
    private EditText mExportNameEditText;
    private Button mExportButton;
    private Button mJumpToSettingPageButton;

    private Switch mMultiPageModeSwitch;
    private Switch mShowMutiPageModeSwitch;
    private Switch mAutoCaptureModeSwitch;
    private Switch mShowAutoCaptureModeShowSwitch;

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
        mStartScanButton = findViewById(R.id.btn_start_scan);
        mExportNameEditText = findViewById(R.id.et_export_name);
        mExportButton = findViewById(R.id.btn_export);
        mJumpToSettingPageButton = findViewById(R.id.btn_jump_to_setting_page);

        mMultiPageModeSwitch = findViewById(R.id.switch_multi_page);
        mShowMutiPageModeSwitch = findViewById(R.id.switch_show_multi_page_mode);
        mAutoCaptureModeSwitch = findViewById(R.id.switch_auto_capture_mode);
        mShowAutoCaptureModeShowSwitch = findViewById(R.id.switch_show_auto_capture_mode);

    }

    private void initData(){
        mHTScanner = HTScanner.instance();
    }

    private void initListener(){
        mInitScannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int code = mHTScanner.initScanner(MainActivity.this.getApplication(),
                        mInputLicenseEditText.getText().toString().trim());
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
                            updateTip("scan success");
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
                String exportDir = dir.getAbsolutePath();
                int exportType = HTScannerExportType.HT_SCANNER_EXPORT_TYPE_IMAGE;
                if(mExportTypePdfRadioButton.isChecked()){
                    exportType = HTScannerExportType.HT_SCANNER_EXPORT_TYPE_PDF;
                }
                String exportName = mExportNameEditText.getText().toString().trim();
                int code = mHtScannerProject.export(exportDir, exportName, exportType, new HTScannerExportCallback() {
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
    }

    private void updateTip(String msg){
        mTipTextView.setText(msg);
    }
}
