// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.ionhiveserde.configuration;

import com.amazon.ion.IonStruct;
import com.amazon.ion.IonType;
import com.amazon.ionhiveserde.AbstractIonHiveSerDe;
import com.amazon.ionhiveserde.configuration.source.JavaPropertiesAdapter;
import com.amazon.ionhiveserde.configuration.source.RawConfiguration;
import com.amazon.ionpathextraction.PathExtractor;
import java.util.List;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;

/**
 * Encapsulates all SerDe properties.
 */
public class SerDeProperties extends BaseProperties {

    private final EncodingConfig encodingConfig;
    private final FailOnOverflowConfig failOnOverflowConfig;
    private final PathExtractionConfig pathExtractionConfig;
    private final SerializeAsConfig serializeAsConfig;
    private final SerializeNullConfig serializeNullConfig;
    private final TimestampOffsetConfig timestampOffsetConfig;
    private final List<String> columnNames;
    private final List<TypeInfo> columnTypes;

    /**
     * Constructor.
     *
     * @param properties {@link Properties} passed to
     *                   {@link AbstractIonHiveSerDe#initialize(Configuration, Properties)}.
     * @param columnNames table column names in the same order as types
     * @param columnTypes table column types in the same order as names
     */
    public SerDeProperties(final Properties properties,
                    final List<String> columnNames,
                    final List<TypeInfo> columnTypes) {
        this(new JavaPropertiesAdapter(properties), columnNames, columnTypes);
    }

    private SerDeProperties(final RawConfiguration configuration,
                            final List<String> columnNames,
                            final List<TypeInfo> columnTypes) {
        super(configuration);

        encodingConfig = new EncodingConfig(configuration);
        failOnOverflowConfig = new FailOnOverflowConfig(configuration, columnNames);
        pathExtractionConfig = new PathExtractionConfig(configuration, columnNames);
        serializeAsConfig = new SerializeAsConfig(configuration, columnTypes);
        serializeNullConfig = new SerializeNullConfig(configuration);
        timestampOffsetConfig = new TimestampOffsetConfig(configuration);
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<TypeInfo> getColumnTypes() {
        return columnTypes;
    }

    /**
     * @see EncodingConfig#getEncoding()
     * @return configured {@link IonEncoding}.
     */
    public IonEncoding getEncoding() {
        return encodingConfig.getEncoding();
    }


    /**
     * @see TimestampOffsetConfig#getTimestampOffsetInMinutes()
     * @return configured offset in minutes.
     */
    public int getTimestampOffsetInMinutes() {
        return timestampOffsetConfig.getTimestampOffsetInMinutes();
    }

    /**
     * @see SerializeNullConfig#getSerializeNull()
     * @return configured {@link SerializeNullStrategy}.
     */
    public SerializeNullStrategy getSerializeNull() {
        return serializeNullConfig.getSerializeNull();
    }


    /**
     * @see FailOnOverflowConfig#failOnOverflowFor(String)
     * @param columnName table column name.
     * @return true if it must fail on overflow, false otherwise.
     */
    public boolean failOnOverflowFor(final String columnName) {
        return failOnOverflowConfig.failOnOverflowFor(columnName);
    }

    /**
     * @see SerializeAsConfig#serializationIonTypeFor(int)
     * @param index table column index.
     * @return IonType to use when serializing values of the respective column.
     */
    public IonType serializationIonTypeFor(final int index) {
        return serializeAsConfig.serializationIonTypeFor(index);
    }

    /**
     * @see PathExtractionConfig#pathExtractor()
     * @return configured {@link PathExtractor}
     */
    public PathExtractor<IonStruct> pathExtractor() {
        return pathExtractionConfig.pathExtractor();
    }

    /**
     * @return Boolean that indicates if the path extractor is configured case sensitive
     */
    public Boolean pathExtractorCaseSensitivity() {
        return pathExtractionConfig.getCaseSensitivity();
    }
}

