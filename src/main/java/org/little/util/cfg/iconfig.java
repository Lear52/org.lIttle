package org.little.util.cfg;
import org.little.util.Except;
/**
 * @author av
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface iconfig {
	/**
	 * Открытие темы в файле конфигурации
	 * @param sub_cfg - ссылка на общий файл конфигурации
	 * @param topic префикс темы ([sis.]   sis.basa.)
	 */
	public abstract void open(iconfig sub_cfg, String topic);

	/**
	 * Открытие файла конфигурации
	 * @param resourse_file - файл конфигурации
	 */
	public abstract void open(String resourse_file) throws Except;

	/**
	 * закрытие файла конфигурации
	 */
	public abstract void close();

	/**
	 * Получение строки по заголовку
	 *
	 * @param item - заголовок строки
	 */
	public abstract String get(String item) throws Except;
	public abstract String getTopic();
        public abstract long getLong(String item);
        public abstract int getInt(String item);
}