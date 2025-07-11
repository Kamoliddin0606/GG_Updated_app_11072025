package com.kashtansystem.project.gloriyamarketing.models.listener;

import android.content.Intent;
import android.view.View;

/**
 * Created by FlameKaf on 28.08.2017.
 * ----------------------------------
 * Событие нажатия на одну из кнопок диалогового окна, запрашивающее действие пользователя.
 */

public interface OnDialogBtnsClickListener
{
    void onBtnClick(View view, Intent intent);
}