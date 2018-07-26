package com.xema.shopmanager.common;

import android.support.annotation.NonNull;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by xema0 on 2018-07-24.
 */

public class SchemaMigration implements RealmMigration {
    @Override
    public void migrate(@NonNull DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        // 버전 1로 마이그레이션
        if (oldVersion == 0) {
            //RealmObjectSchema salesSchema = schema.get("Sales");
            //if (salesSchema != null)
            //    salesSchema.addField("type", String.class);
            //oldVersion++;
        }

    }
}
