package org.odata4j.core;

import java.util.List;

import org.core4j.Enumerable;
import org.core4j.Predicate1;
import org.odata4j.edm.EdmEntitySet;

public class OEntities {

    public static OEntity create(EdmEntitySet entitySet, List<OProperty<?>> properties, List<OLink> links, Object id) {
        return new OEntityImpl(entitySet, properties, links, id);
    }

    public static OEntity create(List<OProperty<?>> properties, List<OLink> links, String title, String categoryTerm) {
        return new OEntityAtomImpl(null, properties, links, title, categoryTerm);
    }

    private static class OEntityAtomImpl extends OEntityImpl implements AtomInfo {

        private final String title;
        private final String categoryTerm;

        public OEntityAtomImpl(EdmEntitySet entitySet, List<OProperty<?>> properties, List<OLink> links, String title, String categoryTerm) {
            super(entitySet, properties, links, null);
            this.title = title;
            this.categoryTerm = categoryTerm;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getCategoryTerm() {
            return categoryTerm;
        }
    }

    private static class OEntityImpl implements OEntity {

    	private final EdmEntitySet entitySet;
        private final List<OProperty<?>> properties;
        private final List<OLink> links;
        private final Object id;

        public OEntityImpl(EdmEntitySet entitySet, List<OProperty<?>> properties, List<OLink> links, Object id) {
        	this.entitySet = entitySet;
            this.properties = properties;
            this.links = links;
            this.id = id;
        }

        @Override
        public String toString() {
            return "OEntity[" + Enumerable.create(getProperties()).join(",") + "]";
        }
        
		@Override
		public EdmEntitySet getEntitySet() {
			return entitySet;
		}

        @Override
        public List<OProperty<?>> getProperties() {
            return properties;
        }

        @Override
        public OProperty<?> getProperty(final String propName) {
            return Enumerable.create(properties).first(new Predicate1<OProperty<?>>() {

                public boolean apply(OProperty<?> input) {
                    return input.getName().equals(propName);
                }
            });
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> OProperty<T> getProperty(String propName, Class<T> propClass) {
            return (OProperty<T>) getProperty(propName);
        }
        
        
        @Override
        public List<OLink> getLinks() {
            return links;
        }
        
        @SuppressWarnings("unchecked")
		@Override
        public <T extends OLink> T getLink(String title, Class<T> linkClass) {
           for(OLink link : getLinks())
               if (link.getTitle().equals(title))
                   return (T)link;
           throw new IllegalArgumentException("No link with title: " + title);
        }

		@Override
		public Object getId() {
			return id;
		}

    }
}
