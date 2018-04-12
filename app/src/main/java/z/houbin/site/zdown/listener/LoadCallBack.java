package z.houbin.site.zdown.listener;

import z.houbin.site.zdown.module.BaseModule;

public interface LoadCallBack {
    public void onLoadStart(BaseModule module);
    public void onLoadEnd(BaseModule module);
    public void onLoadError(BaseModule module,Exception e);
}
