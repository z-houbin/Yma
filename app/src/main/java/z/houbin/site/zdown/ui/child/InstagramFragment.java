package z.houbin.site.zdown.ui.child;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import z.houbin.site.zdown.R;
import z.houbin.site.zdown.module.BaseModule;
import z.houbin.site.zdown.module.Instagram;
import z.houbin.site.zdown.ui.InstagramWebActivity;

/**
 * Instagram
 */
public class InstagramFragment extends BaseFragment {
    private Instagram instagram = new Instagram();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_ins, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        instagram.setLoadListener(this);
        setLabel("Instagram");
    }

    @Override
    public void onDownloadClick(View v) {
        super.onDownloadClick(v);
        if (getInput() != null) {
            if (getInput().contains("https://www.instagram.com/p/")) {
                instagram.parse(getInput());
            } else if (getInput().startsWith("https://www.instagram.com/")) {
                //个人主页
            }
        }
    }

    @Override
    public void onLoadEnd(final BaseModule module) {
        super.onLoadEnd(module);
        handler.post(new Runnable() {
            @Override
            public void run() {
                showInfo(module);
            }
        });
    }

    @Override
    protected void showInfo(final BaseModule module) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(module.getInfo().author);

        StringBuilder msg = new StringBuilder();
        msg.append(module.getInfo().content);
        msg.append("\r\n");
        msg.append("\r\n");
        msg.append("图片").append(module.getInfo().image.size());
        if (!TextUtils.isEmpty(module.getInfo().video)) {
            msg.append(",视频1");
            msg.append("\r\n");
        }
        builder.setMessage(msg.toString());
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                module.download();
            }
        });
        builder.create().show();
        //Intent intent = new Intent(getActivity(), InstagramWebActivity.class);
        //startActivity(intent);
    }
}
