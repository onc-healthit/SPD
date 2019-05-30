package com.esacinc.spd.model.complex_extensions;

import ca.uhn.fhir.model.api.IElement;
import ca.uhn.fhir.model.api.IExtension;
import ca.uhn.fhir.model.api.annotation.Block;
import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.primitive.IdDt;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.Reference;

import java.util.List;

public interface INewPatients {

    @Block
    class VhDirNewPatients implements IExtension {

        /**
         * A boolean
         */
        @Child(name = "acceptingPatients", type = {BooleanType.class}, min=1, max=1, modifier=false, summary=true)
        @Extension(url = "acceptingPatients", definedLocally = false, isModifier = false)
        @Description(shortDefinition="Accepting patients", formalDefinition="Accepting patients" )
        protected BooleanType acceptingPatients;

        /**
         * A reference to a VhDirNetwork
         */
        @Child(name = "network", type = {Reference.class}, min=1, max=1, modifier=false, summary=true)
        @Extension(url = "network", definedLocally = false, isModifier = false)
        @Description(shortDefinition="Network", formalDefinition="Network" )
        protected Reference network;

        public void setAcceptingPatients(boolean acceptingPatients) {
            this.acceptingPatients = new BooleanType(acceptingPatients);
        }

        public void setNetwork(Reference network) {
            this.network = network;
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
