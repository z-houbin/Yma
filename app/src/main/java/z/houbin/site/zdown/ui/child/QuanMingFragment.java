package z.houbin.site.zdown.ui.child;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import z.houbin.site.zdown.R;

/**
 * 全民K歌
 */
public class QuanMingFragment extends BaseFragment{
    private EditText iEditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_qmkg,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iEditText = view.findViewById(R.id.edit);
    }
}
