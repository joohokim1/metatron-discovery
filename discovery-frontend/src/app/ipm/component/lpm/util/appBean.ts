import { Injectable, Injector } from "@angular/core";

import { UIEvent } from "../util/event";

@Injectable()
export class AppBean {

    private eventCtrl: any;

    constructor(
        private injector: Injector
    ) {
        this.eventCtrl = new UIEvent(this);
    }

    public getEventCtrl(): any {
        return this.eventCtrl;
    }
}