package com.android.music.ui.views.multisheet;


import com.jakewharton.rxrelay2.PublishRelay;
import com.simplecity.multisheetview.ui.view.MultiSheetView;

import javax.inject.Inject;

import io.reactivex.Observable;

public class MultiSheetEventRelay {

    private PublishRelay<MultiSheetEvent> eventRelay = PublishRelay.create();

    @Inject
    public MultiSheetEventRelay() {
    }

    public void sendEvent(MultiSheetEvent event) {
        eventRelay.accept(event);
    }

    public Observable<MultiSheetEvent> getEvents() {
        return eventRelay;
    }

    public static class MultiSheetEvent {

        public @interface Action {
            int GOTO = 0;
            int HIDE = 1;
            int SHOW_IF_HIDDEN = 2;
        }

        @Action
        public int action;

        @MultiSheetView.Sheet
        public int sheet;

        public MultiSheetEvent(int action, int sheet) {
            this.action = action;
            this.sheet = sheet;
        }
    }
}
