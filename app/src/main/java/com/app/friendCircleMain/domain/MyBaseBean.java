package com.app.friendCircleMain.domain;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * @author GURR 2014-6-6
 */
public class MyBaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5130475605812561575L;

	protected String addString() {
		String t = "";
		Field[] fields = this.getClass().getDeclaredFields();

		for (Field field : fields) {

			try {
				// if (field.get(o) instanceof File[]) {
				// map.put(j, (File[]) field.get(o));
				// }
				field.setAccessible(true);
				if (field.get(this) != null)
					t += field.getName() + "=" + field.get(this).toString()
							+ "|";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return t;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub

		return addString();
	}
}
