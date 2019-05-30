package com.esacinc.spd.model.complex_extensions;

import ca.uhn.fhir.model.api.IElement;
import ca.uhn.fhir.model.api.IExtension;
import ca.uhn.fhir.model.api.annotation.Block;
import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.primitive.DecimalDt;
import ca.uhn.fhir.model.primitive.IdDt;

import java.util.List;

public interface IGeoLocation {

    @Block
    class VhDirGeoLocation implements IExtension {

        @Child(name = "latitude", min = 1, order = 0, summary = true)
        @Extension(url = "latitude",
                definedLocally = false,
                isModifier = false)
        @Description(shortDefinition = "Latitude. The value domain and the interpretation are the same as for the " +
                "text of the latitude element in KML")
        private DecimalDt latitude;

        @Child(name = "longitude", min = 1, order = 1, summary = true)
        @Extension(url = "longitude",
                definedLocally = false,
                isModifier = false)
        @Description(shortDefinition = "Longitude. The value domain and the interpretation are the same as for the " +
                "text of the latitude element in KML")
        private DecimalDt longitude;

        public void setLatitude(double latitude) {
            this.latitude = new DecimalDt(latitude);
        }

        public void setLongitude(double longitude) {
            this.longitude = new DecimalDt(longitude);
        }

        @Override
        public <T extends IElement> List<T> getAllPopulatedChildElementsOfType(Class<T> aClass) {
            return null;
        }

        @Override
        public String getElementSpecificId() {
            return null;
        }

        @Override
        public IdDt getId() {
            return null;
        }

        @Override
        public void setElementSpecificId(String s) {

        }

        @Override
        public void setId(IdDt idDt) {

        }

        @Override
        public void setId(String s) {

        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean hasFormatComment() {
            return false;
        }

        @Override
        public List<String> getFormatCommentsPre() {
            return null;
        }

        @Override
        public List<String> getFormatCommentsPost() {
            return null;
        }
    }
}
