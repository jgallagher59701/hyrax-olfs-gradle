/*
 * /////////////////////////////////////////////////////////////////////////////
 * // This file is part of the "Hyrax Data Server" project.
 * //
 * //
 * // Copyright (c) 2015 OPeNDAP, Inc.
 * // Author: Nathan David Potter  <ndp@opendap.org>
 * //
 * // This library is free software; you can redistribute it and/or
 * // modify it under the terms of the GNU Lesser General Public
 * // License as published by the Free Software Foundation; either
 * // version 2.1 of the License, or (at your option) any later version.
 * //
 * // This library is distributed in the hope that it will be useful,
 * // but WITHOUT ANY WARRANTY; without even the implied warranty of
 * // MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * // Lesser General Public License for more details.
 * //
 * // You should have received a copy of the GNU Lesser General Public
 * // License along with this library; if not, write to the Free Software
 * // Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * //
 * // You can contact OPeNDAP, Inc. at PO Box 112, Saunderstown, RI. 02874-0112.
 * /////////////////////////////////////////////////////////////////////////////
 */
package opendap.threddsHandler;

import opendap.namespaces.THREDDS;
import org.jdom.Element;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ndp on 4/19/15.
 */
public class AddTimeCoverage {

    private String _datasetNameMatchPattern;
    private String _datasetPathMatchPattern;
    private String _startTimeSubstitutionPattern;
    private String _duration;
    private boolean _isInit;

    private String _parentCatalogPath;


    public AddTimeCoverage(Element addTimeCoverage, String parentCatalogPath) {

        _isInit = false;
        if (addTimeCoverage != null &&  addTimeCoverage.getName().equals(THREDDS.ADD_TIME_COVERAGE)) {
             _datasetNameMatchPattern = addTimeCoverage.getAttributeValue(THREDDS.DATASET_NAME_MATCH_PATTERN);
             _datasetPathMatchPattern = addTimeCoverage.getAttributeValue(THREDDS.DATASET_PATH_MATCH_PATTERN);
             _startTimeSubstitutionPattern = addTimeCoverage.getAttributeValue(THREDDS.START_TIME_SUBSTITUTION_PATTERN);
             _duration = addTimeCoverage.getAttributeValue(THREDDS.DURATION);

            if( (_datasetNameMatchPattern!=null || _datasetPathMatchPattern!=null) &&
                    _startTimeSubstitutionPattern!=null &&
                    _duration!=null ) {

                _isInit = true;
            }
        }

        _parentCatalogPath = parentCatalogPath;




    }
    public Element getTimeCoverage(String name){

        if(!_isInit)
            return null;

        String startTime = getStartTime(name);

        if(startTime == null)
            return null;

        Element timeCoverage = new Element(THREDDS.TIME_COVERAGE,THREDDS.NS);
        Element start = new Element(THREDDS.START,THREDDS.NS);
        Element duration = new Element(THREDDS.DURATION,THREDDS.NS);

        timeCoverage.addContent(start);
        timeCoverage.addContent(duration);

        start.setText(startTime);
        duration.setText(getDuration());

        return timeCoverage;

    }


    String getStartTime(String name) {


        if (name == null)
            return null;


        String matchPatternString = _datasetNameMatchPattern;
        String stringToMatch = name;


        if(matchPatternString==null) {
            matchPatternString = _datasetPathMatchPattern;
            stringToMatch = _parentCatalogPath + name;
        }

        Pattern matchPattern = Pattern.compile(matchPatternString);

        Matcher matcher = matchPattern.matcher(stringToMatch);

        if (!matcher.find())
            return null;

        StringBuffer newName = new StringBuffer();
        matcher.appendReplacement(newName, _startTimeSubstitutionPattern);
        newName.delete(0, matcher.start());

        if (newName.length() == 0)
            return null;

        return newName.toString();

    }

    String getDuration() {
        return _duration;
    }


}
