/*******************************************************************************
 *   Gisgraphy Project 
 * 
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 * 
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 * 
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 * 
 *  Copyright 2008  Gisgraphy project 
 *  David Masclet <davidmasclet@gisgraphy.com>
 *  
 *  
 *******************************************************************************/
package com.gisgraphy.domain.valueobject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import com.gisgraphy.domain.geoloc.entity.*;

/**
 * List of all feature codes with their feature class with a localized
 * description and an associated java Object.<br>
 * A {@link FeatureCode} is represented by a stringwith this pattern :
 * featureclass_featureCode
 * 
 * @see <a href="http://download.geonames.org/export/dump/featureCodes.txt">The
 *      feature code files</a>
 * @see #getObject().
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public enum FeatureCode {
    A_ADM1 {
	@Override
	public GisFeature getObject() {
	    return new Adm(1);
	}
    },
    A_ADM2 {
	@Override
	public GisFeature getObject() {
	    return new Adm(2);
	}
    },
    A_ADM3 {
	@Override
	public GisFeature getObject() {
	    return new Adm(3);
	}
    },
    A_ADM4 {
	@Override
	public GisFeature getObject() {
	    return new Adm(4);
	}
    },
    A_ADMD {
	@Override
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    A_LTER {
	@Override
	public GisFeature getObject() {
	    return new PoliticalEntity();
	}
    },
    A_PCL {
	@Override
	public GisFeature getObject() {
	    return new Country();
	}
    },
    A_PCLD {
	@Override
	public GisFeature getObject() {
	    return new PoliticalEntity();
	}
    },
    A_PCLF {
	@Override
	public GisFeature getObject() {
	    return new PoliticalEntity();
	}
    },
    A_PCLI {
	@Override
	public GisFeature getObject() {
	    return new Country();
	}
    },
    A_PCLIX {
	@Override
	public GisFeature getObject() {
	    return new PoliticalEntity();
	}
    },
    A_PCLS {
	@Override
	public GisFeature getObject() {
	    return new PoliticalEntity();
	}
    },
    A_PRSH {
	@Override
	public GisFeature getObject() {
	    return new Religious();
	}
    },
    A_TERR {
	@Override
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    A_ZN {
	@Override
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    A_ZNB {
	@Override
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    H_AIRS {
	@Override
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    H_ANCH {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_BAY {
	@Override
	public GisFeature getObject() {
	    return new Bay();
	}
    },
    H_BAYS {
	@Override
	public GisFeature getObject() {
	    return new Bay();
	}
    },
    H_BGHT {
	@Override
	public GisFeature getObject() {
	    return new Bay();
	}
    },
    H_BNK {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_BNKR {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_BNKX {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_BOG {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_CAPG {
	@Override
	public GisFeature getObject() {
	    return new Ice();
	}
    },
    H_CHN {
	@Override
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_CHNL {
	@Override
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_CHNM {
	@Override
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_CHNN {
	@Override
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_CNFL {
	@Override
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_CNL {
	@Override
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_CNLA {
	@Override
	public GisFeature getObject() {
	    return new Aqueduc();
	}
    },
    H_CNLB {
	@Override
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_CNLD {
	@Override
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_CNLI {
	@Override
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_CNLN {
	@Override
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_CNLQ {
	@Override
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_CNLSB {
	@Override
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_CNLX {
	@Override
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_COVE {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_CRKT {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_CRNT {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_CUTF {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_DCK {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_DCKB {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_DOMG {
	@Override
	public GisFeature getObject() {
	    return new Ice();
	}
    },
    H_DPRG {
	@Override
	public GisFeature getObject() {
	    return new Ice();
	}
    },
    H_DTCH {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_DTCHD {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_DTCHI {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_DTCHM {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_ESTY {
	@Override
	public GisFeature getObject() {
	    return new Bay();
	}
    },
    H_FISH {
	@Override
	public GisFeature getObject() {
	    return new FishingArea();
	}
    },
    H_FJD {
	@Override
	public GisFeature getObject() {
	    return new Fjord();
	}
    },
    H_FJDS {
	@Override
	public GisFeature getObject() {
	    return new Fjord();
	}
    },
    H_FLLS {
	@Override
	public GisFeature getObject() {
	    return new Falls();
	}
    },
    H_FLLSX {
	@Override
	public GisFeature getObject() {
	    return new Falls();
	}
    },
    H_FLTM {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_FLTT {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_GLCR {
	@Override
	public GisFeature getObject() {
	    return new Ice();
	}
    },
    H_GULF {
	@Override
	public GisFeature getObject() {
	    return new Gulf();
	}
    },
    H_GYSR {
	@Override
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    H_HBR {
	@Override
	public GisFeature getObject() {
	    return new Port();
	}
    },
    H_HBRX {
	@Override
	public GisFeature getObject() {
	    return new Port();
	}
    },
    H_INLT {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_INLTQ {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_LBED {
	@Override
	public GisFeature getObject() {
	    return new Lake();
	}
    },
    H_LGN {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_LGNS {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_LGNX {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_LK {
	@Override
	public GisFeature getObject() {
	    return new Lake();
	}
    },
    H_LKC {
	@Override
	public GisFeature getObject() {
	    return new Lake();
	}
    },
    H_LKI {
	@Override
	public GisFeature getObject() {
	    return new Lake();
	}
    },
    H_LKN {
	@Override
	public GisFeature getObject() {
	    return new Lake();
	}
    },
    H_LKNI {
	@Override
	public GisFeature getObject() {
	    return new Lake();
	}
    },
    H_LKO {
	@Override
	public GisFeature getObject() {
	    return new Lake();
	}
    },
    H_LKOI {
	@Override
	public GisFeature getObject() {
	    return new Lake();
	}
    },
    H_LKS {
	@Override
	public GisFeature getObject() {
	    return new Lake();
	}
    },
    H_LKSB {
	@Override
	public GisFeature getObject() {
	    return new Lake();
	}
    },
    H_LKSC {
	@Override
	public GisFeature getObject() {
	    return new Lake();
	}
    },
    H_LKSI {
	@Override
	public GisFeature getObject() {
	    return new Lake();
	}
    },
    H_LKSN {
	@Override
	public GisFeature getObject() {
	    return new Lake();
	}
    },
    H_LKSNI {
	@Override
	public GisFeature getObject() {
	    return new Lake();
	}
    },
    H_LKX {
	@Override
	public GisFeature getObject() {
	    return new Lake();
	}
    },
    H_MFGN {
	@Override
	public GisFeature getObject() {
	    return new Pond();
	}
    },
    H_MGV {
	@Override
	public GisFeature getObject() {
	    return new Pond();
	}
    },
    H_MOOR {
	@Override
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    H_MRSH {
	@Override
	public GisFeature getObject() {
	    return new Pond();
	}
    },
    H_MRSHN {
	@Override
	public GisFeature getObject() {
	    return new Pond();
	}
    },
    H_NRWS {
	@Override
	public GisFeature getObject() {
	    return new Pond();
	}
    },
    H_OCN {
	@Override
	public GisFeature getObject() {
	    return new Ocean();
	}
    },
    H_OVF {
	@Override
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_PND {
	@Override
	public GisFeature getObject() {
	    return new Pond();
	}
    },
    H_PNDI {
	@Override
	public GisFeature getObject() {
	    return new Pond();
	}
    },
    H_PNDN {
	@Override
	public GisFeature getObject() {
	    return new Pond();
	}
    },
    H_PNDNI {
	@Override
	public GisFeature getObject() {
	    return new Pond();
	}
    },
    H_PNDS {
	@Override
	public GisFeature getObject() {
	    return new Pond();
	}
    },
    H_PNDSF {
	@Override
	public GisFeature getObject() {
	    return new Pond();
	}
    },
    H_PNDSI {
	@Override
	public GisFeature getObject() {
	    return new Pond();
	}
    },
    H_PNDSN {
	public GisFeature getObject() {
	    return new Pond();
	}
    },
    H_POOL {
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_POOLI {
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_RCH {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_RDGG {
	public GisFeature getObject() {
	    return new Ice();
	}
    },
    H_RDST {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    H_RF {
	public GisFeature getObject() {
	    return new Reef();
	}
    },
    H_RFC {
	public GisFeature getObject() {
	    return new Reef();
	}
    },
    H_RFX {
	public GisFeature getObject() {
	    return new Reef();
	}
    },
    H_RPDS {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_RSV {
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_RSVI {
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_RSVT {
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_RVN {
	public GisFeature getObject() {
	    return new Ravin();
	}
    },
    H_SBKH {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    H_SD {
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_SEA {
	public GisFeature getObject() {
	    return new Sea();
	}
    },
    H_SHOL {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    H_SILL {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    H_SPNG {
	public GisFeature getObject() {
	    return new Spring();
	}
    },
    H_SPNS {
	public GisFeature getObject() {
	    return new Spring();
	}
    },
    H_SPNT {
	public GisFeature getObject() {
	    return new Spring();
	}
    },
    H_STM {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_STMA {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_STMB {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_STMC {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_STMD {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_STMH {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_STMI {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_STMIX {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_STMM {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_STMQ {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_STMS {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_STMSB {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_STMX {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_STRT {
	public GisFeature getObject() {
	    return new Strait();
	}
    },
    H_SWMP {
	public GisFeature getObject() {
	    return new Marsh();
	}
    },
    H_SYSI {
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    H_TNLC {
	public GisFeature getObject() {
	    return new Tunnel();
	}
    },
    H_WAD {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_WADB {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_WADJ {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_WADM {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_WADS {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_WADX {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_WHRL {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    H_WLL {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    H_WLLQ {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    H_WLLS {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    H_WTLD {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    H_WTLDI {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    H_WTRC {
	public GisFeature getObject() {
	    return new Stream();
	}
    },
    H_WTRH {
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    L_AGRC {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    L_AMUS {
	public GisFeature getObject() {
	    return new AmusePark();
	}
    },
    L_AREA {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    L_BSND {
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    L_BSNP {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    L_BTL {
	public GisFeature getObject() {
	    return new Military();
	}
    },
    L_CLG {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    L_CMN {
	public GisFeature getObject() {
	    return new Park();
	}
    },
    L_CNS {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    L_COLF {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    L_CONT {
	public GisFeature getObject() {
	    return new Continent();
	}
    },
    L_CST {
	public GisFeature getObject() {
	    return new Coast();
	}
    },
    L_CTRB {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    L_DEVH {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    L_FLD {
	public GisFeature getObject() {
	    return new Field();
	}
    },
    L_FLDI {
	public GisFeature getObject() {
	    return new Field();
	}
    },
    L_GASF {
	public GisFeature getObject() {
	    return new Field();
	}
    },
    L_GRAZ {
	public GisFeature getObject() {
	    return new Field();
	}
    },
    L_GVL {
	public GisFeature getObject() {
	    return new Field();
	}
    },
    L_INDS {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    L_LAND {
	public GisFeature getObject() {
	    return new Ice();
	}
    },
    L_LCTY {
	public GisFeature getObject() {
	    return new City();
	}
    },
    L_MILB {
	public GisFeature getObject() {
	    return new Military();
	}
    },
    L_MNA {
	public GisFeature getObject() {
	    return new Mine();
	}
    },
    L_MVA {
	public GisFeature getObject() {
	    return new Military();
	}
    },
    L_NVB {
	public GisFeature getObject() {
	    return new Military();
	}
    },
    L_OAS {
	public GisFeature getObject() {
	    return new Oasis();
	}
    },
    L_OILF {
	public GisFeature getObject() {
	    return new Field();
	}
    },
    L_PEAT {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    L_PRK {
	public GisFeature getObject() {
	    return new Park();
	}
    },
    L_PRT {
	public GisFeature getObject() {
	    return new Port();
	}
    },
    L_QCKS {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    L_REP {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    L_RES {
	public GisFeature getObject() {
	    return new Reserve();
	}
    },
    L_RESA {
	public GisFeature getObject() {
	    return new Reserve();
	}
    },
    L_RESF {
	public GisFeature getObject() {
	    return new Forest();
	}
    },
    L_RESH {
	public GisFeature getObject() {
	    return new Reserve();
	}
    },
    L_RESN {
	public GisFeature getObject() {
	    return new Reserve();
	}
    },
    L_RESP {
	public GisFeature getObject() {
	    return new Reserve();
	}
    },
    L_RESV {
	public GisFeature getObject() {
	    return new Reserve();
	}
    },
    L_RESW {
	public GisFeature getObject() {
	    return new Reserve();
	}
    },
    L_RGN {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    L_RGNE {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    L_RGNL {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    L_RNGA {
	public GisFeature getObject() {
	    return new Military();
	}
    },
    L_SALT {
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    L_SNOW {
	public GisFeature getObject() {
	    return new Ice();
	}
    },
    L_TRB {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    L_ZZZZZ {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    P_PPL {
	public GisFeature getObject() {
	    return new City();
	}
    },
    P_PPLA {
	public GisFeature getObject() {
	    return new City();
	}
    },
    P_PPLA2 {
	public GisFeature getObject() {
	    return new City();
	}
    },
    P_PPLA3 {
	public GisFeature getObject() {
	    return new City();
	}
    },
    P_PPLA4 {
	public GisFeature getObject() {
	    return new City();
	}
    },
    P_PPLC {
	public GisFeature getObject() {
	    return new City();
	}
    },
    P_PPLG {
	public GisFeature getObject() {
	    return new City();
	}
    },
    P_PPLL {
	public GisFeature getObject() {
	    return new City();
	}
    },
    P_PPLQ {
	public GisFeature getObject() {
	    return new City();
	}
    },
    P_PPLR {
	public GisFeature getObject() {
	    return new City();
	}
    },
    P_PPLS {
	public GisFeature getObject() {
	    return new City();
	}
    },
    P_PPLW {
	public GisFeature getObject() {
	    return new City();
	}
    },
    P_PPLX {
	public GisFeature getObject() {
	    return new CitySubdivision();
	}
    },
    P_STLMT {
	public GisFeature getObject() {
	    return new City();
	}
    },
    R_CSWY {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    R_CSWYQ {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    R_OILP {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    R_PRMN {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    R_PTGE {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    R_RD {
	public GisFeature getObject() {
	    return new Road();
	}
    },
    R_RDA {
	public GisFeature getObject() {
	    return new Road();
	}
    },
    R_RDB {
	public GisFeature getObject() {
	    return new Road();
	}
    },
    R_RDCUT {
	public GisFeature getObject() {
	    return new Road();
	}
    },
    R_RDJCT {
	public GisFeature getObject() {
	    return new Road();
	}
    },
    R_RJCT {
	public GisFeature getObject() {
	    return new Rail();
	}
    },
    R_RR {
	public GisFeature getObject() {
	    return new Rail();
	}
    },
    R_RRQ {
	public GisFeature getObject() {
	    return new Rail();
	}
    },
    R_RTE {
	public GisFeature getObject() {
	    return new Road();
	}
    },
    R_RYD {
	public GisFeature getObject() {
	    return new Rail();
	}
    },
    R_ST {
	public GisFeature getObject() {
	    return new Street();
	}
    },
    R_STKR {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    R_TNL {
	public GisFeature getObject() {
	    return new Tunnel();
	}
    },
    R_TNLN {
	public GisFeature getObject() {
	    return new Tunnel();
	}
    },
    R_TNLRD {
	public GisFeature getObject() {
	    return new Tunnel();
	}
    },
    R_TNLRR {
	public GisFeature getObject() {
	    return new Tunnel();
	}
    },
    R_TNLS {
	public GisFeature getObject() {
	    return new Tunnel();
	}
    },
    R_TRL {
	public GisFeature getObject() {
	    return new Road();
	}
    },
    S_ADMF {
	public GisFeature getObject() {
	    return new Building();
	}
    },
    S_AGRF {
	public GisFeature getObject() {
	    return new Building();
	}
    },
    S_AIRB {
	public GisFeature getObject() {
	    return new Building();
	}
    },
    S_AIRF {
	public GisFeature getObject() {
	    return new Airport();
	}
    },
    S_AIRH {
	public GisFeature getObject() {
	    return new Airport();
	}
    },
    S_AIRP {
	public GisFeature getObject() {
	    return new Airport();
	}
    },
    S_AIRQ {
	public GisFeature getObject() {
	    return new Airport();
	}
    },
    S_AMTH {
	public GisFeature getObject() {
	    return new Theater();
	}
    },
    S_ANS {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_ARCH {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_ASTR {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_ASYL {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_ATHF {
	public GisFeature getObject() {
	    return new Stadium();
	}
    },
    S_ATM {
	public GisFeature getObject() {
	    return new ATM();
	}
    },
    S_BANK {
	public GisFeature getObject() {
	    return new Bank();
	}
    },
    S_BCN {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_BDG {
	public GisFeature getObject() {
	    return new Bridge();
	}
    },
    S_BDGQ {
	public GisFeature getObject() {
	    return new Bridge();
	}
    },
    S_BLDG {
	public GisFeature getObject() {
	    return new Pond();
	}
    },
    S_BP {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_BRKS {
	public GisFeature getObject() {
	    return new Military();
	}
    },
    S_BRKW {
	public GisFeature getObject() {
	    return new Military();
	}
    },
    S_BSTN {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_BTYD {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_BUR {
	public GisFeature getObject() {
	    return new Cemetery();
	}
    },
    S_BUSTN {
	public GisFeature getObject() {
	    return new BusStation();
	}
    },
    S_BUSTP {
	public GisFeature getObject() {
	    return new BusStation();
	}
    },
    S_CARN {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_CAVE {
	public GisFeature getObject() {
	    return new Vineyard();
	}
    },
    S_CH {
	public GisFeature getObject() {
	    return new Religious();
	}
    },
    S_CMP {
	public GisFeature getObject() {
	    return new Camp();
	}
    },
    S_CMPL {
	public GisFeature getObject() {
	    return new Camp();
	}
    },
    S_CMPLA {
	public GisFeature getObject() {
	    return new Camp();
	}
    },
    S_CMPMN {
	public GisFeature getObject() {
	    return new Mine();
	}
    },
    S_CMPO {
	public GisFeature getObject() {
	    return new Camp();
	}
    },
    S_CMPQ {
	public GisFeature getObject() {
	    return new Camp();
	}
    },
    S_CMPRF {
	public GisFeature getObject() {
	    return new Camp();
	}
    },
    S_CMTY {
	public GisFeature getObject() {
	    return new Cemetery();
	}
    },
    S_COMC {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_CRRL {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_CSNO {
	public GisFeature getObject() {
	    return new Casino();
	}
    },
    S_CSTL {
	public GisFeature getObject() {
	    return new Castle();
	}
    },
    S_CSTM {
	public GisFeature getObject() {
	    return new CustomsPost();
	}
    },
    S_CTHSE {
	public GisFeature getObject() {
	    return new CourtHouse();
	}
    },
    S_CTRA {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_CTRCM {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_CTRF {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_CTRM {
	public GisFeature getObject() {
	    return new Hospital();
	}
    },
    S_CTRR {
	public GisFeature getObject() {
	    return new Religious();
	}
    },
    S_CTRS {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_CVNT {
	public GisFeature getObject() {
	    return new Religious();
	}
    },
    S_DAM {
	public GisFeature getObject() {
	    return new Dam();
	}
    },
    S_DAMQ {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_DAMSB {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_DARY {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_DCKD {
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    S_DCKY {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_DIKE {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_DPOF {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_EST {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_ESTB {
	public GisFeature getObject() {
	    return new Plantation();
	}
    },
    S_ESTC {
	public GisFeature getObject() {
	    return new Plantation();
	}
    },
    S_ESTO {
	public GisFeature getObject() {
	    return new Plantation();
	}
    },
    S_ESTR {
	public GisFeature getObject() {
	    return new Plantation();
	}
    },
    S_ESTSG {
	public GisFeature getObject() {
	    return new Plantation();
	}
    },
    S_ESTSL {
	public GisFeature getObject() {
	    return new Plantation();
	}
    },
    S_ESTT {
	public GisFeature getObject() {
	    return new Plantation();
	}
    },
    S_ESTX {
	public GisFeature getObject() {
	    return new Plantation();
	}
    },
    S_FCL {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_FNDY {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_FRM {
	public GisFeature getObject() {
	    return new Farm();
	}
    },
    S_FRMQ {
	public GisFeature getObject() {
	    return new Farm();
	}
    },
    S_FRMS {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_FRMT {
	public GisFeature getObject() {
	    return new Farm();
	}
    },
    S_FT {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_FY {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_GATE {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_GDN {
	public GisFeature getObject() {
	    return new Garden();
	}
    },
    S_GHSE {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_GOSP {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_GRVE {
	public GisFeature getObject() {
	    return new Cemetery();
	}
    },
    S_HERM {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_HLT {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_HSE {
	public GisFeature getObject() {
	    return new House();
	}
    },
    S_HSEC {
	public GisFeature getObject() {
	    return new House();
	}
    },
    S_HSP {
	public GisFeature getObject() {
	    return new Hospital();
	}
    },
    S_HSPC {
	public GisFeature getObject() {
	    return new Hospital();
	}
    },
    S_HSPD {
	public GisFeature getObject() {
	    return new Religious();
	}
    },
    S_HSPL {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_HSTS {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_HTL {
	public GisFeature getObject() {
	    return new Hotel();
	}
    },
    S_HUT {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_HUTS {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_INSM {
	public GisFeature getObject() {
	    return new Military();
	}
    },
    S_ITTR {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_JTY {
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    S_LDNG {
	public GisFeature getObject() {
	    return new Quay();
	}
    },
    S_LEPC {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_LIBR {
	public GisFeature getObject() {
	    return new Library();
	}
    },
    S_LOCK {
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    S_LTHSE {
	public GisFeature getObject() {
	    return new LightHouse();
	}
    },
    S_MALL {
	public GisFeature getObject() {
	    return new Mall();
	}
    },
    S_MAR {
	public GisFeature getObject() {
	    return new Port();
	}
    },
    S_MFG {
	public GisFeature getObject() {
	    return new Factory();
	}
    },
    S_MFGB {
	public GisFeature getObject() {
	    return new Factory();
	}
    },
    S_MFGC {
	public GisFeature getObject() {
	    return new Factory();
	}
    },
    S_MFGCU {
	public GisFeature getObject() {
	    return new Factory();
	}
    },
    S_MFGLM {
	public GisFeature getObject() {
	    return new Factory();
	}
    },
    S_MFGM {
	public GisFeature getObject() {
	    return new Factory();
	}
    },
    S_MFGPH {
	public GisFeature getObject() {
	    return new Factory();
	}
    },
    S_MFGQ {
	public GisFeature getObject() {
	    return new Factory();
	}
    },
    S_MFGSG {
	public GisFeature getObject() {
	    return new Factory();
	}
    },
    S_MKT {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_ML {
	public GisFeature getObject() {
	    return new Mill();
	}
    },
    S_MLM {
	public GisFeature getObject() {
	    return new Mine();
	}
    },
    S_MLO {
	public GisFeature getObject() {
	    return new Factory();
	}
    },
    S_MLSG {
	public GisFeature getObject() {
	    return new Factory();
	}
    },
    S_MLSGQ {
	public GisFeature getObject() {
	    return new Factory();
	}
    },
    S_MLSW {
	public GisFeature getObject() {
	    return new Mill();
	}
    },
    S_MLWND {
	public GisFeature getObject() {
	    return new Mill();
	}
    },
    S_MLWTR {
	public GisFeature getObject() {
	    return new Mill();
	}
    },
    S_MN {
	public GisFeature getObject() {
	    return new Mine();
	}
    },
    S_MNAU {
	public GisFeature getObject() {
	    return new Mine();
	}
    },
    S_MNC {
	public GisFeature getObject() {
	    return new Mine();
	}
    },
    S_MNCR {
	public GisFeature getObject() {
	    return new Mine();
	}
    },
    S_MNCU {
	public GisFeature getObject() {
	    return new Mine();
	}
    },
    S_MNDT {
	public GisFeature getObject() {
	    return new Mine();
	}
    },
    S_MNFE {
	public GisFeature getObject() {
	    return new Mine();
	}
    },
    S_MNMT {
	public GisFeature getObject() {
	    return new Monument();
	}
    },
    S_MNN {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_MNNI {
	public GisFeature getObject() {
	    return new Mine();
	}
    },
    S_MNPB {
	public GisFeature getObject() {
	    return new Mine();
	}
    },
    S_MNPL {
	public GisFeature getObject() {
	    return new Mine();
	}
    },
    S_MNQ {
	public GisFeature getObject() {
	    return new Mine();
	}
    },
    S_MNQR {
	public GisFeature getObject() {
	    return new Mole();
	}
    },
    S_MNSN {
	public GisFeature getObject() {
	    return new Mine();
	}
    },
    S_MOLE {
	public GisFeature getObject() {
	    return new Mole();
	}
    },
    S_MSQE {
	public GisFeature getObject() {
	    return new Religious();
	}
    },
    S_MSSN {
	public GisFeature getObject() {
	    return new Religious();
	}
    },
    S_MSSNQ {
	public GisFeature getObject() {
	    return new Religious();
	}
    },
    S_MSTY {
	public GisFeature getObject() {
	    return new Religious();
	}
    },
    S_MTRO {
	public GisFeature getObject() {
	    return new MetroStation();
	}
    },
    S_MUS {
	public GisFeature getObject() {
	    return new Museum();
	}
    },
    S_NOV {
	public GisFeature getObject() {
	    return new Religious();
	}
    },
    S_NSY {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_OBPT {
	public GisFeature getObject() {
	    return new ObservatoryPoint();
	}
    },
    S_OBS {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_OBSR {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_OILJ {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_OILQ {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_OILR {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_OILT {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_OILW {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_OPRA {
	public GisFeature getObject() {
	    return new OperaHouse();
	}
    },
    S_PAL {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_PGDA {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_PIER {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_PKLT {
	public GisFeature getObject() {
	    return new Parking();
	}
    },
    S_PMPO {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_PMPW {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_PO {
	public GisFeature getObject() {
	    return new PostOffice();
	}
    },
    S_PP {
	public GisFeature getObject() {
	    return new PolicePost();
	}
    },
    S_PPQ {
	public GisFeature getObject() {
	    return new PolicePost();
	}
    },
    S_PRKGT {
	public GisFeature getObject() {
	    return new Parking();
	}
    },
    S_PRKHQ {
	public GisFeature getObject() {
	    return new Parking();
	}
    },
    S_PRN {
	public GisFeature getObject() {
	    return new Prison();
	}
    },
    S_PRNJ {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_PRNQ {
	public GisFeature getObject() {
	    return new Prison();
	}
    },
    S_PS {
	public GisFeature getObject() {
	    return new Factory();
	}
    },
    S_PSH {
	public GisFeature getObject() {
	    return new Factory();
	}
    },
    S_PSTB {
	public GisFeature getObject() {
	    return new CustomsPost();
	}
    },
    S_PSTC {
	public GisFeature getObject() {
	    return new CustomsPost();
	}
    },
    S_PSTP {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_PYR {
	public GisFeature getObject() {
	    return new Pyramid();
	}
    },
    S_PYRS {
	public GisFeature getObject() {
	    return new Pyramid();
	}
    },
    S_QUAY {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_RECG {
	public GisFeature getObject() {
	    return new Golf();
	}
    },
    S_RECR {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_REST {
	public GisFeature getObject() {
	    return new Restaurant();
	}
    },
    S_RHSE {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_RKRY {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_RLG {
	public GisFeature getObject() {
	    return new Religious();
	}
    },
    S_RLGR {
	public GisFeature getObject() {
	    return new Religious();
	}
    },
    S_RNCH {
	public GisFeature getObject() {
	    return new Ranch();
	}
    },
    S_RSD {
	public GisFeature getObject() {
	    return new Rail();
	}
    },
    S_RSGNL {
	public GisFeature getObject() {
	    return new Rail();
	}
    },
    S_RSRT {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_RSTN {
	public GisFeature getObject() {
	    return new RailRoadStation();
	}
    },
    S_RSTNQ {
	public GisFeature getObject() {
	    return new RailRoadStation();
	}
    },
    S_RSTP {
	public GisFeature getObject() {
	    return new Rail();
	}
    },
    S_RSTPQ {
	public GisFeature getObject() {
	    return new Rail();
	}
    },
    S_RUIN {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_SCH {
	public GisFeature getObject() {
	    return new School();
	}
    },
    S_SCHA {
	public GisFeature getObject() {
	    return new School();
	}
    },
    S_SCHC {
	public GisFeature getObject() {
	    return new School();
	}
    },
    S_SCHM {
	public GisFeature getObject() {
	    return new Military();
	}
    },
    S_SCHN {
	public GisFeature getObject() {
	    return new School();
	}
    },
    S_SHPF {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_SHRN {
	public GisFeature getObject() {
	    return new Religious();
	}
    },
    S_SHSE {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_SLCE {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_SNTR {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_SPA {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_SPLY {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_SQR {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_STBL {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_STDM {
	public GisFeature getObject() {
	    return new Stadium();
	}
    },
    S_STNB {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_STNC {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_STNE {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_STNF {
	public GisFeature getObject() {
	    return new Forest();
	}
    },
    S_STNI {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_STNM {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_STNR {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_STNS {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_STNW {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_STPS {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_THTR {
	public GisFeature getObject() {
	    return new Theater();
	}
    },
    S_TMB {
	public GisFeature getObject() {
	    return new Cemetery();
	}
    },
    S_TMPL {
	public GisFeature getObject() {
	    return new Religious();
	}
    },
    S_TNKD {
	public GisFeature getObject() {
	    return new WaterBody();
	}
    },
    S_TOWR {
	public GisFeature getObject() {
	    return new Tower();
	}
    },
    S_TRIG {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_TRMO {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_UNIV {
	public GisFeature getObject() {
	    return new School();
	}
    },
    S_USGE {
	public GisFeature getObject() {
	    return new Building();
	}
    },
    S_VETF {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_WALL {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_WALLA {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_WEIR {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_WHRF {
	public GisFeature getObject() {
	    return new Quay();
	}
    },
    S_WRCK {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_WTRW {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_ZNF {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    S_ZOO {
	public GisFeature getObject() {
	    return new Zoo();
	}
    },
    T_ASPH {
	public GisFeature getObject() {
	    return new Lake();
	}
    },
    T_ATOL {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_BAR {
	public GisFeature getObject() {
	    return new Bar();
	}
    },
    T_BCH {
	public GisFeature getObject() {
	    return new Beach();
	}
    },
    T_BCHS {
	public GisFeature getObject() {
	    return new Beach();
	}
    },
    T_BDLD {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_BLDR {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_BLHL {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_BLOW {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_BNCH {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_BUTE {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_CAPE {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_CFT {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_CLDA {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_CLF {
	public GisFeature getObject() {
	    return new Cliff();
	}
    },
    T_CNYN {
	public GisFeature getObject() {
	    return new Canyon();
	}
    },
    T_CONE {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_CRDR {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_CRQ {
	public GisFeature getObject() {
	    return new Cirque();
	}
    },
    T_CRQS {
	public GisFeature getObject() {
	    return new Cirque();
	}
    },
    T_CRTR {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_CUET {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_DLTA {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_DPR {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_DSRT {
	public GisFeature getObject() {
	    return new Desert();
	}
    },
    T_DUNE {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_DVD {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_ERG {
	public GisFeature getObject() {
	    return new Desert();
	}
    },
    T_FAN {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_FORD {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_FSR {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_GAP {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_GRGE {
	public GisFeature getObject() {
	    return new Gorge();
	}
    },
    T_HDLD {
	public GisFeature getObject() {
	    return new Bay();
	}
    },
    T_HLL {
	public GisFeature getObject() {
	    return new Hill();
	}
    },
    T_HLLS {
	public GisFeature getObject() {
	    return new Hill();
	}
    },
    T_HMCK {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_HMDA {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_INTF {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_ISL {
	public GisFeature getObject() {
	    return new Island();
	}
    },
    T_ISLF {
	public GisFeature getObject() {
	    return new Island();
	}
    },
    T_ISLM {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_ISLS {
	public GisFeature getObject() {
	    return new Island();
	}
    },
    T_ISLT {
	public GisFeature getObject() {
	    return new Island();
	}
    },
    T_ISLX {
	public GisFeature getObject() {
	    return new Island();
	}
    },
    T_ISTH {
	public GisFeature getObject() {
	    return new Island();
	}
    },
    T_KRST {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_LAVA {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_LEV {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_MESA {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_MND {
	public GisFeature getObject() {
	    return new Mound();
	}
    },
    T_MRN {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_MT {
	public GisFeature getObject() {
	    return new Mountain();
	}
    },
    T_MTS {
	public GisFeature getObject() {
	    return new Mountain();
	}
    },
    T_NKM {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_NTK {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_NTKS {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_PAN {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_PANS {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_PASS {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_PEN {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_PENX {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_PK {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_PKS {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_PLAT {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_PLATX {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_PLDR {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_PLN {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_PLNX {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_PROM {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_PT {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_PTS {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_RDGB {
	public GisFeature getObject() {
	    return new Beach();
	}
    },
    T_RDGE {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_REG {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_RK {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_RKFL {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_RKS {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_SAND {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_SBED {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_SCRP {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_SDL {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_SHOR {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_SINK {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_SLID {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_SLP {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_SPIT {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_SPUR {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_TAL {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_TRGD {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_TRR {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_UPLD {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_VAL {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_VALG {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_VALS {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_VALX {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    T_VLC {
	public GisFeature getObject() {
	    return new Volcano();
	}
    },
    U_APNU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_ARCU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_ARRU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_BDLU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_BKSU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_BNCU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_BNKU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_BSNU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_CDAU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_CNSU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_CNYU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_CRSU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_DEPU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_EDGU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_ESCU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_FANU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_FLTU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_FRKU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_FRSU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_FRZU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_FURU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_GAPU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_GLYU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_HLLU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_HLSU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_HOLU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_KNLU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_KNSU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_LDGU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_LEVU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_MDVU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_MESU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_MNDU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_MOTU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_MTSU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_MTU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_PKSU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_PKU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_PLFU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_PLNU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_PLTU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_PNLU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_PRVU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_RAVU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_RDGU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_RDSU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_RFSU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_RFU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_RISU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_RMPU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_RNGU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_SCNU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_SCSU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_SDLU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_SHFU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_SHLU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_SHSU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_SHVU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_SILU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_SLPU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_SMSU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_SMU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_SPRU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_TERU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_TMSU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_TMTU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_TNGU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_TRGU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_TRNU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_VALU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    U_VLSU {
	public GisFeature getObject() {
	    return new UnderSea();
	}
    },
    V_BUSH {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    V_CULT {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    V_FRST {
	public GisFeature getObject() {
	    return new Forest();
	}
    },
    V_FRSTF {
	public GisFeature getObject() {
	    return new Forest();
	}
    },
    V_GRSLD {
	public GisFeature getObject() {
	    return new GrassLand();
	}
    },
    V_GRVC {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    V_GRVO {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    V_GRVP {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    V_GRVPN {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    V_HTH {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    V_MDW {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    V_OCH {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    V_SCRB {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    V_TREE {
	public GisFeature getObject() {
	    return new Tree();
	}
    },
    V_TUND {
	public GisFeature getObject() {
	    return new GisFeature();
	}
    },
    V_VIN {
	public GisFeature getObject() {
	    return new Vineyard();
	}
    },
    V_VINS {
	public GisFeature getObject() {
	    return new Vineyard();
	}
    },
    UNK_UNK {
	public GisFeature getObject() {
	    return new GisFeature();
	}},
	OSM_ADMBUILDING{
	public GisFeature getObject() {
	    return new AdmBuilding();
	}},
	OSM_BENCH{
	public GisFeature getObject() {
	    return new Bench();
	}},
	OSM_CINEMA{
	public GisFeature getObject() {
	    return new Cinema();
	}},
	OSM_DENTIST{
	public GisFeature getObject() {
	    return new Dentist();
	}},
	OSM_DOCTOR{
	public GisFeature getObject() {
	    return new Doctor();
	}},
	OSM_EMERGENCYPHONE{
	public GisFeature getObject() {
	    return new EmergencyPhone();
	}},
	OSM_FERRYTERMINAL{
	public GisFeature getObject() {
	    return new FerryTerminal();
	}},
	OSM_FIRESTATION{
	public GisFeature getObject() {
	    return new FireStation();
	}},
	OSM_FOUNTAIN{
	public GisFeature getObject() {
	    return new Fountain();
	}},
	OSM_FUEL{
	public GisFeature getObject() {
	    return new Fuel();
	}},
	OSM_NIGHTCLUB{
	public GisFeature getObject() {
	    return new NightClub();
	}},
	OSM_PHARMACY{
	public GisFeature getObject() {
	    return new Pharmacy();
	}},
	OSM_RENTAL{
	public GisFeature getObject() {
	    return new Rental();
	}},
	OSM_SHOP{
	public GisFeature getObject() {
	    return new Shop();
	}},
	OSM_SWIMMINGPOOL{
	public GisFeature getObject() {
	    return new SwimmingPool();
	}},
	OSM_TAXI{
	public GisFeature getObject() {
	    return new Taxi();
	}},
	OSM_TELEPHONE{
	public GisFeature getObject() {
	    return new Telephone();
	}},
	OSM_TOILET{
	public GisFeature getObject() {
	    return new Toilet();
	}},
	OSM_VENDINGMACHINE{
	public GisFeature getObject() {
	    return new VendingMachine();
	}},
	OSM_VETERINARY{
	public GisFeature getObject() {
	    return new Veterinary();
	}},
	UNKNOW {
		public GisFeature getObject() {
		    return new GisFeature();
		}
    };

    /**
     * Returns the java Object that the feature class and feature code are
     * associated to. The goal is to defined java Object that regroup several
     * feature code. <u>note</u> : Having several object improve performance
     * for polymorphic request.<br/> because we restrict the search to the
     * specified type and we don't search in the whole {@link GisFeature}
     * 
     * @return The java Object the featureclass and feature code are associated
     *         to
     */
    public abstract GisFeature getObject();

    /**
     * The specified locale is used : <br/> If null the
     * {@link LocaleContextHolder} one is used (in a web context, it should be
     * the user locale).<br/> If no bundle exists for the
     * {@link LocaleContextHolder} : the {@link Locale#getDefault()} is used.<br/>
     * If no bundle for {@link Locale#getDefault()} exists : the
     * {@link #DEFAULT_FALLBACK_LOCALE} is used. <br/> <br/> If no translation
     * is found {@link #DEFAULT_TRANSLATION} is return
     * 
     * @param locale
     *                The Locale
     * @return The localized description
     */
    public String getLocalizedDescription(Locale locale) {
	if (locale == null) {
	    locale = LocaleContextHolder.getLocale();
	}
	String description = DEFAULT_TRANSLATION;
	try {
	    ResourceBundle bundle = ResourceBundle.getBundle(
		    Constants.FEATURECODE_BUNDLE_KEY, locale);
	    if (bundle.getLocale().getLanguage()!= locale.getLanguage()){
	    	 bundle = ResourceBundle.getBundle(
	    		    Constants.FEATURECODE_BUNDLE_KEY, LocaleContextHolder.getLocale());
	    }
		description = bundle.getString(
		    this.toString());
	} catch (MissingResourceException mse) {
	    logger.warn(this.toString() + " is not localized for : " + locale);
	    try {
		description = ResourceBundle.getBundle(
			Constants.FEATURECODE_BUNDLE_KEY,
			DEFAULT_FALLBACK_LOCALE).getString(this.toString());
	    } catch (RuntimeException e) {
		logger
			.warn(this.toString()
				+ " is not localized for the default fallback translation: "
				+ locale);
		return description;
	    }
	} catch (RuntimeException rte) {
	    logger.warn("Can not retrieve localized description for "
		    + this.toString() + " for locale " + locale + " : "
		    + rte.getMessage());
	}
	return description;
    }

    /**
     * The default Locale, If the thread one does not esxists
     */
    public static final Locale DEFAULT_FALLBACK_LOCALE = Locale.ENGLISH;

    /**
     * The default Translation. It is used when no translation is found for the
     * locale
     */
    public static final String DEFAULT_TRANSLATION = "Toponym";

    /**
     * A map that associate a string to a class
     */
    public final static Map<String, Class<? extends GisFeature>> entityClass = new HashMap<String, Class<? extends GisFeature>>();

    protected static final Logger logger = LoggerFactory
	    .getLogger(FeatureCode.class);

    static {
	Class<? extends GisFeature> clazz = null;
	for (FeatureCode featurecode : FeatureCode.values()) {
	    clazz = (Class<? extends GisFeature>) featurecode.getObject()
		    .getClass();
	    entityClass.put(clazz.getSimpleName().toLowerCase(), clazz);
	}
	logger.info("There is  " + entityClass.size() + " different entities");
	if (logger.isInfoEnabled()) {
	    for (String key : entityClass.keySet()) {
		logger.info("entityclass contains " + key + "="
			+ entityClass.get(key));
	    }
	}
    }
    // TODO v2 test that all feature code are localized
}
