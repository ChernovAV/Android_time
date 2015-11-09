package com.chernov.android.android_time.DataBase;

import com.j256.ormlite.field.DatabaseField;

/**
 *  Объект, который мы создаем и сохранение в базе данных.
 */
public class Simple {

	// Первичным ключем может служить любое поле — достаточно указать у него id = true, но
	// рекомендуется сделать автогенерируемое значение id и поставить ему generatedId = true.
	// ORMLite сама назначит ему уникальный номер.
	//	@DatabaseField(generatedId = true)
	//	int id;

	@DatabaseField
	String artist;

	Simple() {
		// needed by ormlite
	}

	public Simple(String artist) {
		this.artist = artist;
	}

	@Override
	public String toString() {
		return artist;
	}
}
