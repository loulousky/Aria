/*
 * Copyright (C) 2016 AriaLyy(https://github.com/AriaLyy/Aria)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arialyy.aria.core.download;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.arialyy.aria.core.AriaManager;
import com.arialyy.aria.core.download.group.DownloadGroupUtil;
import com.arialyy.aria.core.download.group.FtpDirDownloadUtil;
import com.arialyy.aria.core.download.group.IDownloadGroupListener;
import com.arialyy.aria.core.inf.AbsGroupTask;
import com.arialyy.aria.core.inf.AbsTaskWrapper;
import com.arialyy.aria.core.scheduler.ISchedulers;
import com.arialyy.aria.util.CheckUtil;

/**
 * Created by AriaL on 2017/6/27.
 * 任务组任务
 */
public class DownloadGroupTask extends AbsGroupTask<DownloadGroupEntity, DGTaskWrapper> {

  private DownloadGroupTask(DGTaskWrapper taskWrapper, Handler outHandler) {
    mTaskWrapper = taskWrapper;
    mOutHandler = outHandler;
    mContext = AriaManager.APP;
    mListener = new DownloadGroupListener(this, mOutHandler);
    switch (taskWrapper.getRequestType()) {
      case AbsTaskWrapper.D_HTTP:
        mUtil = new DownloadGroupUtil((IDownloadGroupListener) mListener, mTaskWrapper);
        break;
      case AbsTaskWrapper.D_FTP_DIR:
        mUtil = new FtpDirDownloadUtil((IDownloadGroupListener) mListener, mTaskWrapper);
        break;
    }
  }

  public DownloadGroupEntity getEntity() {
    return mTaskWrapper.getEntity();
  }

  @Override public String getTaskName() {
    return "任务组->" + (TextUtils.isEmpty(mTaskWrapper.getEntity().getAlias())
        ? mTaskWrapper.getEntity().getGroupHash() : mTaskWrapper.getEntity().getAlias());
  }

  @Override public int getTaskType() {
    return DOWNLOAD_GROUP;
  }

  public static class Builder {
    DGTaskWrapper taskEntity;
    Handler outHandler;

    public Builder(DGTaskWrapper taskEntity) {
      CheckUtil.checkTaskEntity(taskEntity);
      this.taskEntity = taskEntity;
    }

    /**
     * 设置自定义Handler处理下载状态时间
     *
     * @param schedulers {@link ISchedulers}
     */
    public DownloadGroupTask.Builder setOutHandler(ISchedulers schedulers) {
      outHandler = new Handler(Looper.getMainLooper(), schedulers);
      return this;
    }

    public DownloadGroupTask build() {
      return new DownloadGroupTask(taskEntity, outHandler);
    }
  }
}
