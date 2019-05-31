package com.esacinc.spd.model.complex_extensions;

import ca.uhn.fhir.model.api.IElement;
import ca.uhn.fhir.model.api.IExtension;
import ca.uhn.fhir.model.api.annotation.Block;
import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.primitive.IdDt;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.TimeType;

import java.util.ArrayList;
import java.util.List;

public interface IAvailableTime {

    @Block
    class VhDirAvailableTime implements IExtension {

        public VhDirAvailableTime() {
            super();
        }

        @Child(name="daysOfWeek", max= org.hl7.fhir.r4.model.annotations.Child.MAX_UNLIMITED)
        @Extension(url="daysOfWeek", definedLocally=true, isModifier=false)
        @Description(shortDefinition="Days of the week - mon|tue|wed|thu|fri|sat|sun")
        private List<CodeType> daysOfWeek;

        @Child(name="allDay", max=1)
        @Extension(url="allDay", definedLocally=true, isModifier=false)
        @Description(shortDefinition="Always available? e.g. 24 hour service")
        private BooleanType allDay;

        @Child(name="availableStartTime", max=1)
        @Extension(url="availableStartTime", definedLocally=true, isModifier=false)
        @Description(shortDefinition="Opening time of day (ignored if allDay = true)")
        private TimeType availableStartTime;

        @Child(name="availableEndTime", max=1)
        @Extension(url="availableEndTime", definedLocally=true, isModifier=false)
        @Description(shortDefinition="Closing time of day (ignored if allDay = true)")
        private TimeType availableEndTime;

        public List<CodeType> getDaysOfWeek() {
            if (this.daysOfWeek == null)
                this.daysOfWeek = new ArrayList<>();
            return this.daysOfWeek;
        }

        public BooleanType getAllDay() {
            if (this.allDay == null)
                this.allDay = new BooleanType();
            return this.allDay;
        }

        public TimeType getAvailableStartTime() {
            if (this.availableStartTime == null)
                this.availableStartTime = new TimeType();
            return this.availableStartTime;
        }

        public TimeType getAvailableEndTime() {
            if (this.availableEndTime == null)
                this.availableEndTime = new TimeType();
            return this.availableEndTime;
        }

        public void addDaysOfWeek(CodeType t) {
            if (this.daysOfWeek == null)
                this.daysOfWeek = new ArrayList<>();
            this.daysOfWeek.add(t);
        }

        public void setAllDay(boolean allDay) {
            this.allDay = new BooleanType(allDay);
        }

        public void setAvailableStartTime(TimeType availableStartTime) {
            this.availableStartTime = availableStartTime;
        }

        public void setAvailableEndTime(TimeType availableEndTime) {
            this.availableEndTime = availableEndTime;
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
    }
}
