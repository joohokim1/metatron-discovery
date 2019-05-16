INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr01", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", null, null, null, "가입 정보", null, null, "2", "N", "N");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr01001", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr03", null, "scrbr_sex_nm", "가입 유형", "multiple", "회선 가입자 명의의 성별을 의미합니다.", "401", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr01002", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr01", null, "scrbr_age", "가입자 연령", "range", "회선 가입자 명의의 '만' 나이를 의미합니다.", "202", "Y", "N");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr01003", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr01", null, "use_pred", "사용기간", "range", "단위는 '개월'이며, "조회한 날짜 - 가입일자"로 계산됩니다.", "203", "Y", "N");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr01004", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr01", "inv_sido_nm|inv_sgg_nm|inv_dong_nm", "inv_ldong_cd", "청구지 주소", "address", "회선 가입자 명의의 요금 청구지 주소를 의미합니다.", "204", "Y", "N");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr01005", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr11", "bas_prpn_nm", "bas_prpn_cd", "기본 요금제", "list", "회선 가입자 명의의 기본 요금제를 의미합니다.", "101", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr01006", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr01", null, "mbrp_gr_nm", "멤버십 등급", "multiple", "회선 가입자 명의의 T멤버십 등급을 의미합니다.", "206", "Y", "N");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr01007", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr01", null, "lmth_arpu", "전월 ARPU", "range", "회선 가입자 명의의 전월 ARPU 금액으로, 매월 10일에 월단위 데이터가 갱신됩니다.
* ARPU(Average Revenue Per User) : 가입자당평균매출. 가입한 서비스에 대해 가입자 1명이 특정 기간 동안 지출한 평균 금액을 의미합니다.", "207", "Y", "N");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr02", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", null, null, null, "사용자 정보", null, null, "3", "N", "N");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr02001", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr02", null, "user_estmt_sex_nm", "실사용자 성별 추정", "multiple", "전사 CPM에서 제공하는 데이터로, 예측모델을 통해 실제 회선 사용자의 성별을 추정한 값입니다. (월단위 갱신)", "301", "Y", "N");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr02002", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr02", null, "user_estmt_age", "실사용자 연령 추정", "range", "전사 CPM에서 제공하는 데이터로, 예측모델을 통해 실제 회선 사용자의 연령을 추정한 값입니다. (월단위 갱신)", "302", "Y", "N");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr02003", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr02", "estmt_comp_sido_nm|estmt_comp_sgg_nm|estmt_comp_do", "estmt_comp_ldong_cd", "추정 직장 주소", "address", "매월1일을 기준으로 최근 2주간 회선의 위치 데이터를 활용하여 평일 낮 시간대에 가장 오래 체류한 법정동 정보를 제공합니다.
* 전사 CPM에서 제공하는 데이터를 사용하고 있으며, 해당 기간에 위치 데이터가 발생하지 않는 회선은 제공되지 않습니다.", "303", "Y", "N");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr02004", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr02", null, "estmt_comp_wkdy_stay_time", "추정 직장 평일 체류시간", "range", "매월1일을 기준으로 최근 2주간 회선의 위치 데이터를 활용하여 평일에 직장 추정 위치에서 체류한 일평균 시간을 의미합니다.
* 전사 CPM에서 제공하는 데이터를 사용하고 있으며, 해당 기간에 위치 데이터가 발생하지 않는 회선은 제공되지 않습니다.", "304", "Y", "N");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr02005", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr02", null, "estmt_comp_hday_stay_time", "추정 직장 휴일 체류시간", "range", "매월1일을 기준으로 최근 2주간 회선의 위치 데이터를 활용하여 휴일에 직장 추정 위치에서 체류한 일평균 시간을 의미합니다.
* 전사 CPM에서 제공하는 데이터를 사용하고 있으며, 해당 기간에 위치 데이터가 발생하지 않는 회선은 제공되지 않습니다.", "305", "Y", "N");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr02006", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr02", null, "home_comp_expt_dist_val", "집~직장 거리 추정(km)", "range", "추정 직장 주소와 추정 거주지 주소 간 거리를 계산합니다.
* 전사 CPM에서 제공하는 데이터를 사용하고 있으며, 해당 기간에 위치 데이터가 발생하지 않는 회선은 제공되지 않습니다.", "306", "Y", "N");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr02007", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr02", null, "wkdy_tot_mov_dist_val", "평일 총 이동 거리(km)", "range", "전월 평일에 고객기 이동한 것으로 추정되는 총 거리(km)를 의미합니다.", "307", "Y", "N");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr02008", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr02", null, "hday_tot_mov_dist_val", "휴일 총 이동 거리(km)", "range", "전월 휴일에 고객기 이동한 것으로 추정되는 총 거리(km)를 의미합니다.", "308", "Y", "N");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr03", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", null, null, null, "단말 특성", null, null, "4", "N", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr03001", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr03", null, "ue_typ_nm", "단말기 유형", "multiple", "단말기 유형(스마트폰, 태블릿, Wearable Device, 2nd Device 등)을 의미합니다.", "402", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr03002", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr03", null, "ue_mdl_nm", "단말기 모델명", "list", "단말기 명칭 및 모델명", "403", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr03003", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr03", "ue_vend_nm", "ue_vend_cd", "단말기 제조사", "list", "단말 제조사 코드 및 이름", "404", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr03004", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr03", null, "ue_relse_dt", "단말기 출시일", "calendar", "해당 단말 모델이 출시된 날짜입니다.", "405", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr03005", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr03", null, "ue_fw_ver_nm", "단말 펌웨어 버전", "list", "단말의 Firmware 버전을 의미하며, 단말 모델별 버전 체계가 상이하므로 모델명으로 검색하시길 권장 드립니다.", "406", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr03006", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr03", null, "hdv_spt_ue_yn_cd_nm", "HDV 지원 여부", "multiple", "단말 모델별 HD Voice 지원이 가능한 지 여부를 의미합니다.", "407", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr03007", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr03", null, "hdv_est_yn", "HDV 설정 여부", "multiple", "고객이 HD Voice 기능을 설정했는 지 여부를 의미합니다.", "408", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr03008", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr03", null, "ipv6_spt_yn_nm", "IPv6 지원 여부", "multiple", "단말 모델별 IPv6 지원이 가능한 지 여부를 의미합니다.", "409", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr03009", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr03", null, "ipv6_use_yn", "IPv6 사용 여부", "multiple", "고객 단말이 IPv6로 접속하는 지를 의미합니다. (전체 call의 98% 이상 IPv6로 접속하면 Yes)", "410", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr04", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", null, null, null, "거주지 특성", null, null, "5", "N", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr04001", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr04", "estmt_rsdnc_sido_nm|estmt_rsdnc_sgg_nm|estmt_rsdnc", "estmt_rsdnc_ldong_cd", "추정 실거주지 주소", "address", "최근 2주간 회선의 위치 데이터를 활용하여 평일 밤 시간대에 가장 오래 체류한 법정동 정보를 제공합니다.
* 전사 CPM에서 제공하는 데이터를 사용하고 있으며, 해당 기간에 위치 데이터가 발생하지 않는 회선은 제공되지 않습니다.", "501", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr04002", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr04", null, "rsdnc_qlt_rcnt_day03_rsrp_avg_val", "실거주지 RSRP", "checkrange", "실거주지 RSRP는 AOM데이터를 기반으로 새벽시간(23~07시) 실거주지 추정 주소 반경 2km 내 cell에서의 RSRP 값 평균을 의미합니다.", "502", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr04003", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr04", null, "rsdnc_qlt_sinr_avg_val", "실거주지 SINR", "range", "실거주지 SINR는 AOM데이터를 기반으로 새벽시간(23~07시) 실거주지 추정 주소 반경 2km 내 cell에서의 SINR 값 평균을 의미합니다.", "503", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr04004", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr04", null, "rsdnc_qlt_rsrp_gap_val", "실거주지 RSRP 변화", "range", "조회 날짜를 기준으로 [전일 RSRP - 최근 2주간 RSRP 평균] 값으로 계산됩니다.
* RSRP는 AOM데이터를 기반으로 새벽시간(23~07시) 실거주지 추정 주소 반경 2km 내 cell에서의 값만 반영합니다.", "504", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr04005", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr04", null, "rsdnc_qlt_sinr_gap_val", "실거주지 SINR 변화", "range", "조회 날짜를 기준으로 [전일 SINR - 최근 2주간 SINR 평균] 값으로 계산됩니다.
* RSRP는 AOM데이터를 기반으로 새벽시간(23~07시) 실거주지 추정 주소 반경 2km 내 cell에서의 값만 반영합니다.", "505", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr04006", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr04", null, "home_sped_rpetr_instl_yn", "Speed중계기 여부", "multiple", "Speed중계기 설치 요청을 한 이력이 있는 고객을 의미합니다. (이후 철거된 경우는 미포함)", "506", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr04007", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr04", null, "sped_rpetr_freq_nm", "Speed중계기 주파수", "multiple", "Speed중계기 Type에 따라 지원되는 주파수 정보를 의미합니다.", "507", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr04008", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr04", null, "wkdy_home_stay_time", "평일 댁내 체류시간", "range", "전사 CPM에서 제공하는 데이터로, 평일에 추정 실거주지 내에 체류한 평균 시간을 의미합니다.", "508", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr04009", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr04", null, "hday_home_stay_time", "휴일 댁내 체류시간", "range", "전사 CPM에서 제공하는 데이터로, 휴일에 추정 실거주지 내에 체류한 평균 시간을 의미합니다.", "509", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr05", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", null, null, null, "사용환경변화", null, null, "6", "N", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr05001", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr05", null, "chg_dt", "사용환경변화 일자", "calendar", "신규 및 기변, 해지가 발생한 날짜를 의미합니다. (조회 시점 기준으로는 최근 3개월 전까지 조회 가능하며, 그 이전 데이터는 기준 날짜를 바꿔서 확인 가능합니다.)", "601", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr05002", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr05", null, "chg_typ_nm", "사용환경변화 Type", "multiple", "신규 및 기변, 해지 등 사용환경변화 유형을 의미합니다. (기간을 설정하지 않으면 최근 3개월 내역이 조회됩니다.)", "602", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr05003", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr05", null, "hdv_cei_gap_val", "HDV CEI gap", "range", "[사용환경변화 후 1주간 HDV CEI Summary(UDF) - 사용환경변화 전 1주간 HDV CEI Summary(UDF)] 으로 계산됩니다.", "603", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr05004", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr05", null, "rsrp_gap_val", "RSRP Gap", "range", "[사용환경변화 후 1주간 RSRP 평균 - 사용환경변화 전 1주간 RSRP 평균] 으로 계산됩니다.", "604", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr05005", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr05", null, "voc_scor_gap_val", "VoC 예측 Score Gap", "range", "[사용환경변화 후 1주간 VoC 예측 Score 평균 - 사용환경변화 전 1주간 VoC 예측 Score 평균] 으로 계산됩니다.", "605", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr05006", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr05", null, "hdv_cei_use_env_chg_bf_val", "사용환경변화 전 HDV CEI", "range", "[사용환경변화 전 1주간 HDV CEI Summary(UDF)] 으로 계산됩니다.", "606", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr05007", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr05", null, "hdv_cei_use_env_chg_af_val", "사용환경변화 후 HDV CEI", "range", "[사용환경변화 후 1주간 HDV CEI Summary(UDF)] 으로 계산됩니다.", "607", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr05008", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr05", null, "rsrp_wk2_bf_avg_scor", "사용환경변화 전 RSRP", "range", "[사용환경변화 전 1주간 실거주지 RSRP 평균] 으로 계산됩니다.", "608", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr05009", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr05", null, "rsrp_wk2_af_avg_scor", "사용환경변화 후 RSRP", "range", "[사용환경변화 후 1주간 실거주지 RSRP 평균] 으로 계산됩니다.", "609", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr05010", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr05", null, "voc_scor_wk2_bf_avg_val", "사용환경변화 전 Voc Score", "range", "[사용환경변화 전 1주간 VoC 예측 Score 평균] 으로 계산됩니다.", "610", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr05011", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr05", null, "voc_scor_wk2_af_avg_val", "사용환경변화 후 Voc Score", "range", "[사용환경변화 후 1주간 VoC 예측 Score 평균] 으로 계산됩니다.", "611", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr06", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", null, null, null, "고객 성향", null, null, "7", "N", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr06001", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr06", null, "voc_hst_cnt", "Voc 제기 여부", "range", "최근 한 달 내에 VoC를 제기한 이력이 있는 고객을 의미합니다.", "701", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr06002", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr06", null, "voc_typ_nm", "Voc 유형", "list", "통화품질 VoC 중 "상담유형2"에 해당합니다.", "702", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr06003", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr06", null, "voc_scor_rcnt_day03_avg_val", "Voc 예측 Score", "checkrange", "주거공간에서의 통화품질 부정경험을 토대로 VoC 제기확률을 Scoring한 모형입니다.
   - '주거공간' 통화품질 VOC 제기 이력(Y)에 대한 예측 확률입니다.
   - VoC 제기 유무를 구분지을 수 있는 FEATURE(고객성향, AOM 전파품질, TPANI CEI품질, 주변환경)로 분류 모델을 학습하여 모델을 개발, 이 모형을 통해 고객별 향후 VOC제기 가능성이 높은 고객을 일단위로 스코어링합니다.", "703", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr06004", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr06", null, "voc_scor_impt_cas_nm", "VoC Score Top1 Reason", "list", "VoC 예측 모델에서 VoC Score를 산출할 때 가장 많은 영향을 끼쳤던 Reason을 제공합니다.", "704", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr06005", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr06", null, "ue_chg_ncnt", "단말(IMEI) 변경 횟수", "range", "최근 1주간 단말기(IMEI)가 변경된 횟수를 의미합니다. USIM을 다른 단말에 꽂으면 Count되며, 당일 변경한 건은 반영되지 않습니다.", "705", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr06006", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr06", null, "mms_tm_acpt_scor", "MMS TM 수용성 Score", "range", "전사 CPM에서 제공하는 데이터로, MMS 텔레마케팅을 수용할 가능성을 지수화 한 값입니다.", "706", "Y", "N");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr06007", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr06", null, "skt_tm_acpt_scor", "SKT TM 수용성 스코어", "range", "전사 CPM에서 제공하는 데이터로, SKT의 텔레마케팅을 수용할 가능성을 지수화 한 값입니다.", "707", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr06008", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr06", null, "ob_tm_acpt_scor", "Outbound TM 수용성 Score", "range", "전사 CPM에서 제공하는 데이터로, SKT의 텔레마케팅을 수용할 가능성을 지수화 한 값입니다.", "708", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr07", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", null, null, null, "망 접속패턴", null, null, "8", "N", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr07001", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr07", null, "hdv_main_enb_cell_id", "HDV 주사용 cell", "range", "최근 1주간 가장 많은 시간동안 HDV 호를 접속했던 eNB_Cell을 의미합니다.", "801", "Y", "N");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr07002", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr07", null, "dat_main_enb_cell_id", "Data 주사용 cell", "range", "최근 1주간 가장 많은 시간동안 Data 호를 접속했던 eNB_Cell을 의미합니다.", "802", "Y", "N");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr07003", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr07", null, "dat_main_freq_nm", "주사용 주파수", "multiple", "최근 1주간 가장 많은 시간동안 접속했던 주파수를 의미합니다.", "803", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr07004", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr07", null, "main_cell_hdv_cei_scor", "주사용 cell HDV CEI", "range", "최근 1주간 가장 많은 시간동안 접속했던 eNB_Cell의 HDV CEI Summary(UDF)값을 의미합니다.", "804", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr07005", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr07", null, "main_cell_dat_cei_scor", "주사용 cell Data CEI", "range", "최근 1주간 가장 많은 시간동안 접속했던 eNB_Cell의 Data CEI Summary(UDF)값을 의미합니다.", "805", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr07006", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr07", null, "cnnt_cell_cnt", "접속 cell 수", "range", "[일단위 접속했던 cell 수]에 대한 최근 1주간의 평균값을 의미합니다.", "806", "Y", "N");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr07007", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr07", null, "freq_chg_ncnt", "주파수 변경 횟수", "range", "[일단위 주파수 H/O 횟수]에 대한 최근 1주간의 평균값을 의미합니다.", "807", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr07008", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr07", null, "ue_off_ncnt", "단말기 off 횟수", "range", "최근 1주간 단말기 Power off/비행기 모드를 한 총 횟수를 의미합니다.", "808", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr07009", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr07", null, "ue_off_mntn_perd", "단말기 off 유지 기간", "range", "최근 1주간 단말기 Power off/비행기 모드 상태를 유지한 시간을 의미합니다.", "809", "Y", "N");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", null, null, null, "주파수별 사용 특성", null, null, "9", "N", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08001", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr08", null, "f800m_hdv_cei_scor", "800M HDV CEI", "range", "최근 1주간 800M 주파수 cell에서의 HDV CEI Summary값을 의미합니다.", "901", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08002", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr08", null, "f1_8g_hdv_cei_scor", "1.8G HDV CEI", "range", "최근 1주간 1.8G 주파수 cell에서의 HDV CEI Summary값을 의미합니다.", "902", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08003", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr08", null, "f2_1g_hdv_cei_scor", "2.1G HDV CEI", "range", "최근 1주간 2.1G 주파수 cell에서의 HDV CEI Summary값을 의미합니다.", "903", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08004", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr08", null, "f2_6g_hdv_cei_scor", "2.6G HDV CEI", "range", "최근 1주간 2.6G 주파수 cell에서의 HDV CEI Summary값을 의미합니다.", "904", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08005", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr08", null, "f800m_hdv_et", "800M HDV ET", "range", "최근 1주간 800M 주파수 cell에서의 HDV ET sum 값 [일평균(분)]을 의미합니다.", "905", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08006", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr08", null, "f1_8g_hdv_et", "1.8G HDV ET", "range", "최근 1주간 1.8G 주파수 cell에서의 HDV ET sum 값 [일평균(분)]을 의미합니다.", "906", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08007", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr08", null, "f2_1g_hdv_et", "2.1G HDV ET", "range", "최근 1주간 2.1G 주파수 cell에서의 HDV ET sum 값 [일평균(분)]을 의미합니다.", "907", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08008", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr08", null, "f2_6g_hdv_et", "2.6G HDV ET", "range", "최근 1주간 2.6G 주파수 cell에서의 HDV ET sum 값 [일평균(분)]을 의미합니다.", "908", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08009", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr08", null, "f800m_hdv_attc_cnt", "800M HDV 시도호", "range", "최근 1주간 800M 주파수 cell에서의 HDV 시도호 sum 값 [일평균(분)]을 의미합니다.", "909", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08010", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr08", null, "f1_8g_hdv_attc_cnt", "1.8G HDV 시도호", "range", "최근 1주간 1.8G 주파수 cell에서의 HDV 시도호 sum 값 [일평균(분)]을 의미합니다.", "910", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08011", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr08", null, "f2_1g_hdv_attc_cnt", "2.1G HDV 시도호", "range", "최근 1주간 2.1G 주파수 cell에서의 HDV 시도호 sum 값 [일평균(분)]을 의미합니다.", "911", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08012", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr08", null, "f2_6g_hdv_attc_cnt", "2.6G HDV 시도호", "range", "최근 1주간 2.6G 주파수 cell에서의 HDV 시도호 sum 값 [일평균(분)]을 의미합니다.", "912", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08013", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr08", null, "f800m_dat_cei_scor", "800M Data CEI", "range", "최근 1주간 800M 주파수 cell에서의 Data CEI Summary값을 의미합니다.", "913", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08014", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr08", null, "f1_8g_dat_cei_scor", "1.8G Data CEI", "range", "최근 1주간 1.8G 주파수 cell에서의 Data CEI Summary값을 의미합니다.", "914", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08015", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr08", null, "f2_1g_dat_cei_scor", "2.1G Data CEI", "range", "최근 1주간 2.1G 주파수 cell에서의 Data CEI Summary값을 의미합니다.", "915", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08016", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr08", null, "f2_6g_dat_cei_scor", "2.6G Data CEI", "range", "최근 1주간 2.6G 주파수 cell에서의 Data CEI Summary값을 의미합니다.", "916", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08017", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr08", null, "f800m_dat_et", "800M Data ET", "range", "최근 1주간 800M 주파수 cell에서의 Data ET sum 값 [일평균(분)]을 의미합니다.", "917", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08018", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr08", null, "f1_8g_dat_et", "1.8G Data ET", "range", "최근 1주간 1.8G 주파수 cell에서의 Data ET sum 값 [일평균(분)]을 의미합니다.", "918", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08019", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr08", null, "f2_1g_dat_et", "2.1G Data ET", "range", "최근 1주간 2.1G 주파수 cell에서의 Data ET sum 값 [일평균(분)]을 의미합니다.", "919", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr08020", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr08", null, "f2_6g_dat_et", "2.6G Data ET", "range", "최근 1주간 2.6G 주파수 cell에서의 Data ET sum 값 [일평균(분)]을 의미합니다.", "920", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr09", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", null, null, null, "고객 품질", null, null, "10", "N", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr09001", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr09", null, "dat_cei_rcnt_day03_scor", "LTE CEI", "checkrange", "선택한 기간에 대한 CEI Summary(UDF) 값을 의미합니다.", "1001", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr09002", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr09", null, "hdv_cei_rcnt_day03_scor", "HDV CEI", "checkrange", "선택한 기간에 대한 CEI Summary(UDF) 값을 의미합니다.", "1003", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr09003", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr09", null, "csfb_cei_rcnt_day03_scor", "CSFB CEI", "checkrange", "선택한 기간에 대한 CEI Summary(UDF) 값을 의미합니다.", "1004", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr09004", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr09", null, "g3_rdrt_cnt", "3G천이 건수", "range", "최근 1주간 3G천이가 발생한 건수를 의미합니다.", "1005", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr09005", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr09", null, "g3_rdrt_rate", "3G천이율", "range", "최근 1주간 [3G천이 발생 건수 / 시도호 * 100] 값을 의미합니다.", "1006", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr09006", "2019-04-11 00:00:00.0", "DT-ADMIN", "2019-04-11 00:00:00.0", "DT-ADMIN", "fltr09", null, "g5_dat_cei_rcnt_day03_scor", "5G CEI", "checkrange", "선택한 기간에 대한 5G CEI Summary(UDF) 값을 의미합니다.", "1002", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr10", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", null, null, null, "Traffic 유형", null, null, "11", "N", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr10001", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr10", null, "voce_use_rate", "음성 사용 비율", "range", "최근 1주간 [(HDV_ET + CSFB_ET) * 100 / (TOT_ET)] 값을 의미합니다.", "1101", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr10002", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr10", null, "voce_use_time", "음성 사용 시간(분)", "range", "최근 1주간 [일단위 (HDV_ET + CSFB_ET) / 60]의 평균값을 의미합니다.", "1102", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr10003", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr10", null, "csfb_use_rate", "CSFB 사용 비율", "range", "최근 1주간 [CSFB_ET * 100 / (HDV_ET + CSFB_ET)] 값을 의미합니다.", "1103", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr10004", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr10", null, "dat_use_time", "데이터 사용 시간(분)", "range", "최근 1주간 [일단위 Data_ET / 60]의 평균값을 의미합니다.", "1104", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr10005", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr10", null, "dwld_qty", "Download 량(MB)", "range", "최근 1주간 [일단위 Downlink Traffic]의 평균값을 의미합니다. [MB]", "1105", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr10006", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr10", null, "ulad_qty", "Upload 량(MB)", "range", "최근 1주간 [일단위 Uplink Traffic]의 평균값을 의미합니다. [MB]", "1106", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr10007", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr10", null, "tthr_use_rate", "테더링 사용 비율", "range", "최근 1주간 전체 데이터 중 테더링으로 사용된 비율을 의미합니다.", "1107", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr10008", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr10", null, "mltmd_use_rate", "Multimedia 사용 비율", "range", "최근 1주간 전체 데이터 중 Multimedia로 사용된 비율을 의미합니다.
  * T-PANI App. 분류 내역을 참조합니다.", "1108", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr10009", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr10", null, "sns_use_rate", "SNS 사용 비율", "range", "최근 1주간 전체 데이터 중 SNS로 사용된 비율을 의미합니다.
  * T-PANI App. 분류 내역을 참조합니다.", "1109", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr10010", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr10", null, "web_portal_use_rate", "웹포털 사용 비율", "range", "최근 1주간 전체 데이터 중 웹포털로 사용된 비율을 의미합니다.
  * T-PANI App. 분류 내역을 참조합니다.", "1110", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr10011", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr10", null, "game_use_rate", "Game 사용 비율", "range", "최근 1주간 전체 데이터 중 Game으로 사용된 비율을 의미합니다.
  * T-PANI App. 분류 내역을 참조합니다.", "1111", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr10012", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr10", null, "appst_use_rate", "App.Store 사용 비율", "range", "최근 1주간 전체 데이터 중 App.Store로 사용된 비율을 의미합니다.
  * T-PANI App. 분류 내역을 참조합니다.", "1112", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr11", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", null, null, null, "SV 모델", null, null, "1", "N", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr11001", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr11", null, "g3_trf_use_yn", "3G Only 사용여부", "multiple", "조회일자 기준 -7일~전일까지 3G Only 사용여부를 의미합니다.", "102", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr11002", "2018-11-30 00:00:00.0", "DT-ADMIN", "2019-05-07 00:00:00.0", "DT-ADMIN", "fltr11", null, "usim_suspect_7days_count", "USIM 불량 여부", "checkrange", null, "103", "Y", "Y");
INSERT INTO ipm_icpm_fltr_bas
 (fltr_uid, frst_reg_date, frst_reg_user_id, last_chg_date, last_chg_user_id, fltr_grp_uid, fltr_nm, druid_nm, scrn_nm, scrn_cl_nm, fltr_desc, fltr_turn, leaf_yn, use_yn)
VALUES ("fltr11003", "2018-11-30 00:00:00.0", "DT-ADMIN", "2018-11-30 00:00:00.0", "DT-ADMIN", "fltr11", null, null, "WiFi - LTE 핑퐁 여부 (TBD)", null, null, "104", "Y", "T");
