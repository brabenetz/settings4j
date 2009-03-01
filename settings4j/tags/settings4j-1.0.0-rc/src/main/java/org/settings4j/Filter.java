package org.settings4j;

public interface Filter {
	
//	/**
//	 * The simplest default implementation doesn't filter.
//	 */
//	public static final Filter NO_FILTER = new Filter(){
//		public void addExclude(String pattern) {
//			throw new java.lang.IllegalStateException("This instance of Filter cannot be modified.");
//		}
//
//		public void addInclude(String pattern) {
//			throw new java.lang.IllegalStateException("This instance of Filter cannot be modified.");
//		}
//
//		public boolean isValid(String key) {
//			return true;
//		}
//	};
	
	public void addInclude(String pattern);
	public void addExclude(String pattern);
	public boolean isValid(String key);
}
