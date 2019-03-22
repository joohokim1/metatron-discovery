/*
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

import {
  Component, ElementRef, EventEmitter, Injector, Output, ViewChild
} from '@angular/core';
import { DataconnectionService } from '../../dataconnection/service/dataconnection.service';
import { Alert } from '../../common/util/alert.util';
import { SetWorkspacePublishedComponent } from '../component/set-workspace-published/set-workspace-published.component';
import { CommonUtil } from '../../common/util/common.util';
import { CookieConstant } from '../../common/constant/cookie.constant';
import { StringUtil } from '../../common/util/string.util';
import {AbstractComponent} from "../../common/component/abstract.component";
import {ConnectionComponent} from "../component/connection/connection.component";
import {AuthenticationType} from "../../domain/dataconnection/dataconnection";

/**
 * Data connection create component
 */
@Component({
  selector: 'app-create-connection',
  templateUrl: './create-connection.component.html'
})
export class CreateConnectionComponent extends AbstractComponent {

  // workspace set component
  @ViewChild(SetWorkspacePublishedComponent)
  private readonly _setWorkspaceComponent: SetWorkspacePublishedComponent;

  // connection component
  @ViewChild(ConnectionComponent)
  private readonly _connectionComponent: ConnectionComponent;

  // add workspace list
  public addWorkspaces: any[];
  // connection name
  public connectionName: string;
  // name input validation
  public isShowConnectionNameRequired: boolean;
  // name validation message
  public nameErrorMsg: string;
  // workspace published
  public published: boolean;
  // show flag
  public isShowPopup: boolean;

  @Output()
  public readonly createdConnection: EventEmitter<boolean> = new EventEmitter();

  // constructor
  constructor(private connectionService: DataconnectionService,
              protected element: ElementRef,
              protected injector: Injector) {
    super(element, injector);
  }

  /**
   * Init
   */
  public init(): void {
    // init
    this.connectionName = undefined;
    this.nameErrorMsg = undefined;
    this.isShowConnectionNameRequired = undefined;
    this.published = undefined;
    // set private workspace in add workspace list
    this.addWorkspaces = [JSON.parse(this.cookieService.get(CookieConstant.KEY.MY_WORKSPACE))];
    // set connection type list
    // show popup
    this.isShowPopup = true;
    // detect
    this.safelyDetectChanges();
    // set connection data in component
    this._connectionComponent.init();
  }

  /**
   * Done button click event
   */
  public done(): void {
    // set click flag
    this._connectionComponent.isConnectionCheckRequire = true;
    // if enable create connection, create connection
    this._isEnableCreateConnection() && this._createConnection();
  }

  /**
   * Cancel button click event
   */
  public cancel(): void {
    this.isShowPopup = undefined;
  }

  /**
   * Click workspace setting open
   */
  public onClickSetWorkspace(): void {
    this._setWorkspaceComponent.init('connection', 'create', {addWorkspaces: this.addWorkspaces});
  }

  /**
   * Is enable create connection
   * @return {boolean}
   * @private
   */
  private _isEnableCreateConnection(): boolean {
    // check valid connection
    if (!this._connectionComponent.isValidConnection) {
      return false;
    }
    // if empty connection name
    if (StringUtil.isEmpty(this.connectionName)) {
      this.isShowConnectionNameRequired = true;
      this.nameErrorMsg = this.translateService.instant('msg.storage.dconn.name.error');
      return false;
    }
    // if connection name over 150 byte
    else if (CommonUtil.getByte(this.connectionName.trim()) > 150) {
      this.isShowConnectionNameRequired = true;
      this.nameErrorMsg = this.translateService.instant('msg.alert.edit.name.len');
      return false;
    }
    // if exist properties
    if (this._connectionComponent.isExistProperties()) {
      return this._connectionComponent.isValidProperties();
    }
    return true;
  }

  /**
   * Create connection
   * @private
   */
  private _createConnection(): void {
    // loading show
    this.loadingShow();
    // create connection
    this.connectionService.createConnection(this._getCreateConnectionParams())
      .then((result) => {
        // alert
        Alert.success(`'${this.connectionName.trim()}' ` + this.translateService.instant('msg.storage.alert.dconn.create.success'));
        // loading hide
        this.loadingHide();
        // emit
        this.createdConnection.emit(true);
        // close
        this.cancel();
      })
      .catch(error => this.commonExceptionHandler(error));
  }

  /**
   * Get create connection params
   * @return {{implementor: ImplementorType}}
   * @private
   */
  public _getCreateConnectionParams() {
    let result = this._connectionComponent.getConnectionParams();
    result['type'] = 'JDBC';
    result['name'] = this.connectionName.trim();
    result['published'] = this.published;
    // if disable authentication
    if (this._connectionComponent.isDisableAuthenticationType()) {
      result['authenticationType'] = AuthenticationType.MANUAL;
    }
    // if exist properties
    if (this._connectionComponent.isExistProperties()) {
      result['properties'] = this._connectionComponent.getProperties();
    }
    // workspace list
    if (!this.published) {
      result['workspaces'] = this.addWorkspaces.reduce((acc, workspace) => {
        acc.push(`/api/workspaces/${workspace.id}`);
        return acc;
      }, []);
    }
    return result;
  }
}
