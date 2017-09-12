package com.blovvme.wundergroud;

/**
 * Created by Blovvme on 9/11/17.
 */

public interface BasePresenter <V extends BaseView> {
    void attachView(V view);
    void detachView();
}
