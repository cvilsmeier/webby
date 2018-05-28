package org.webby;

/**
 * Classes implementing this interface must have an empty constructor.
 * Example:
 * <pre>
 * public class MyApp implements App {
 *   
 *   public MyApp() {
 *   }
 *   
 *   ...
 * }
 * </pre>
 */
public interface App {

	void init(AppContext appContext) throws Exception;

	void serve(WebRequest req) throws Exception;
	
	void destroy();

}
