import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { NouisliderModule } from 'ng2-nouislider';
import { CommonModule } from '@angular/common';
//import { SharedModule } from '../../portal/common/shared.module';
import { IpmComponent } from './component/ipm.component';
import { MainComponent } from './component/main/main.component';
import { IcpmComponent } from './component/icpm/icpm.component';
import { EpmComponent } from './component/epm/epm.component';
import { LpmComponent } from './component/lpm/lpm.component';
import { EpmListTableComponent } from './component/epm/epm-list-table/epm-list-table.component';
import { EpmDetailTableComponent } from './component/epm/epm-detail-table/epm-detail-table.component';
import { BmrkComponent } from './common/component/bmrk/bmrk.component';
import { RightKindComponent } from './common/component/right-kind/right-kind.component';
import { CheckrangeComponent } from './common/component/right-kind/checkrange/checkrange.component';
import { RangeComponent } from './common/component/right-kind/range/range.component';
import { DruidrangeComponent } from './common/component/right-kind/druidrange/druidrange.component';
import { CalendarComponent } from './common/component/right-kind/calendar/calendar.component';
import { SingleComponent } from './common/component/right-kind/single/single.component';
import { MultipleComponent } from './common/component/right-kind/multiple/multiple.component';
import { WeeklyComponent } from './common/component/right-kind/weekly/weekly.component';
import { YmdhComponent } from './common/component/right-kind/ymdh/ymdh.component';
import { ListComponent } from './common/component/right-kind/list/list.component';
import { RegionComponent } from './common/component/right-kind/region/region.component';
import { OrgComponent } from './common/component/right-kind/org/org.component';
import { EmsComponent } from './common/component/right-kind/ems/ems.component';
import { MtsoComponent } from './common/component/right-kind/mtso/mtso.component';
import { AddressComponent } from './common/component/right-kind/address/address.component';
import { DateTimeComponent } from './common/component/right-kind/datetime/datetime.component';
import { CEIComponent } from './common/component/right-kind/cei/cei.component';
import { SpecificTimeComponent } from './common/component/right-kind/specifictime/sftime.component';
import { HierarchyComponent } from './common/component/right-kind/hierarchy/hierarchy.component';
import { GroupComponent } from './common/component/right-kind/group/group.component';
import { EnbComponent } from './common/component/right-kind/enb/enb.component';
import { CellComponent } from './common/component/right-kind/cell/cell.component';
import { HourComponent } from './common/component/right-kind/hour/hour.component';
import { CustMultiCheckComponent } from './common/component/right-kind/custmulticheck/custMultiCheck.component';

import { SubFilterComponent } from './component/lpm/center/subFilter/subFilter.component';
import { SFCustMultiCheckComponent } from './component/lpm/center/subFilter/sf-custmulticheck/sf_custMultiCheck.component';
import { SFDatetimeComponent } from './component/lpm/center/subFilter/sf-datetime/sf_datetime.component';
import { SFMultipleComponent } from './component/lpm/center/subFilter/sf-multiple/sf_multiple.component';
import { SFRangeComponent } from './component/lpm/center/subFilter/sf-range/sf_range.component';
import { SFCEIComponent } from './component/lpm/center/subFilter/sf-cei/sf_cei.component';

import { IpmService } from './service/ipm.service';
import { IcpmService } from './service/icpm/icpm.service';
import { EpmService } from './service/epm/epm.service';
import { LpmService } from './service/lpm/lpm.service';
import { ChartService } from '../ipm/common/service/chart/chart.service';
import { BmrkService } from './common/service/bmrk/bmrk.service';
import { AddressService } from '../ipm/common/service/right-kind/address/address.service';
import { ListService } from './common/service/right-kind/list/list.service';
import { HierarchyService } from './common/service/right-kind/hierarchy/hierarchy.service';
import { DruidrangeService } from './common/service/right-kind/druidrange/druidrange.service';

import { LPMCenterComponent } from './component/lpm/center/center.component';
import { CenterDefaultComponent } from './component/lpm/center/default/center_default.component';
import { CenterCompareComponent } from './component/lpm/center/compare/center_compare.component';

import { LPMLeftComponent } from './component/lpm/left/left.component';
import { LPMMenuComponent } from './component/lpm/left/menu/lpm_menu.component';
import { LPMPropComponent } from './component/lpm/left/prop/lpm_prop.component';

import { LPMRightComponent } from './component/lpm/right/right.component';
import { RightDefaultComponent } from './component/lpm/right/default/right_default.component';
import { RightCompareComponent } from './component/lpm/right/compare/right_compare.component';

import { LPMSouthComponent } from './component/lpm/south/south.component';
import { SouthDefaultComponent } from './component/lpm/south/default/south_default.component';
import { LPMChartComponent } from './component/lpm/south/chart/lpm_chart.component';
import { LPMGridComponent } from './component/lpm/south/grid/lpm_grid.component';
import { FormsModule } from '@angular/forms';

const ipmRoutes: Routes = [
  { path: '', component: LpmComponent },
  { path: 'icpm', component: IcpmComponent },
  { path: 'epm', component: EpmComponent },
  { path: 'lpm', component: LpmComponent }
];

@NgModule({
  imports: [
    FormsModule,
    RouterModule.forChild(ipmRoutes),
    CommonModule,
    //SharedModule,
    NouisliderModule
  ],
  declarations: [
    IpmComponent,
    MainComponent,
    IcpmComponent,
    EpmComponent,
    LpmComponent,
    EpmListTableComponent,
    EpmDetailTableComponent,
    BmrkComponent,
    RightKindComponent,
    CheckrangeComponent,
    RangeComponent,
    DruidrangeComponent,
    CalendarComponent,
    SingleComponent,
    MultipleComponent,
    WeeklyComponent,
    YmdhComponent,
    ListComponent,
    RegionComponent,
    OrgComponent,
    EmsComponent,
    MtsoComponent,
    AddressComponent,
    DateTimeComponent,
    CEIComponent,
    SpecificTimeComponent,
    HierarchyComponent,
    GroupComponent,
    EnbComponent,
    CellComponent,
    HourComponent,
    CustMultiCheckComponent,

    SubFilterComponent,
    SFCustMultiCheckComponent,
    SFDatetimeComponent,
    SFMultipleComponent,
    SFRangeComponent,
    SFCEIComponent,

    LPMCenterComponent,
    CenterDefaultComponent,
    CenterCompareComponent,

    LPMLeftComponent,
    LPMMenuComponent,
    LPMPropComponent,

    LPMRightComponent,
    RightDefaultComponent,
    RightCompareComponent,

    LPMSouthComponent,
    SouthDefaultComponent,
    LPMChartComponent,
    LPMGridComponent
  ],
  providers: [
    IpmService,
    IcpmService,
    EpmService,
    LpmService,
    ChartService,
    BmrkService,
    AddressService,
    ListService,
    HierarchyService,
    DruidrangeService
  ]
})
export class IpmModule { }
