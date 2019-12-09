package il.co.quana

/**
 * This class includes a subset of standard GATT attributes and carousel image
 * mapping
 */
object GattAttributes {

    /**
     * Services
     */
    val HEART_RATE_SERVICE = "0000180d-0000-1000-8000-00805f9b34fb"
    val DEVICE_INFORMATION_SERVICE = "0000180a-0000-1000-8000-00805f9b34fb"
    val HEALTH_TEMP_SERVICE = "00001809-0000-1000-8000-00805f9b34fb"
    val BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb"
    val IMMEDIATE_ALERT_SERVICE = "00001802-0000-1000-8000-00805f9b34fb"
    val LINK_LOSS_SERVICE = "00001803-0000-1000-8000-00805f9b34fb"
    val TRANSMISSION_POWER_SERVICE = "00001804-0000-1000-8000-00805f9b34fb"
    val BLOOD_PRESSURE_SERVICE = "00001810-0000-1000-8000-00805f9b34fb"
    val SCAN_PARAMETERS_SERVICE = "00001813-0000-1000-8000-00805f9b34fb"

    /**
     * Unused Service UUIDS
     */
    val AUTOMATION_IO_SERVICE = "00001815-0000-1000-8000-00805f9b34fb"
    val CONTINUOUS_TIME_SERVICE = "00001805-0000-1000-8000-00805f9b34fb"
    val HTTP_PROXY_SERVICE = "00001823-0000-1000-8000-00805f9b34fb"
    val INDOOR_POSITIONING_SERVICE = "00001821-0000-1000-8000-00805f9b34fb"
    val INTERNET_PROTOCOL_SERVICE = "00001820-0000-1000-8000-00805f9b34fb"
    val OBJECT_TRANSFER_SERVICE = "00001825-0000-1000-8000-00805f9b34fb"
    val PULSE_OXIMETER_SERVICE = "00001822-0000-1000-8000-00805f9b34fb"
    val TRANSPORT_DISCOVERY = "00001824-0000-1000-8000-00805f9b34fb"
    val USER_DATA = "0000181c-0000-1000-8000-00805f9b34fb"
    val WEIGHT_SCALE = "0000181d-0000-1000-8000-00805f9b34fb"

    val BODY_COMPOSITION_SERVICE = "0000181b-0000-1000-8000-00805f9b34fb"
    val CONTINUOUS_GLUCOSE_MONITORING_SERVICE = "0000181f-0000-1000-8000-00805f9b34fb"
    val CYCLING_POWER_SERVICE = "00001818-0000-1000-8000-00805f9b34fb"
    val ENVIRONMENTAL_SENSING_SERVICE = "0000181a-0000-1000-8000-00805f9b34fb"
    val LOCATION_NAVIGATION_SERVICE = "00001819-0000-1000-8000-00805f9b34fb"
    val USER_DATA_SERVICE = "0000181c-0000-1000-8000-00805f9b34fb"
    val WEIGHT_SCALE_SERVICE = "0000181d-0000-1000-8000-00805f9b34fb"
    val HEALTH_THERMOMETER_SERVICE = "00001809-0000-1000-8000-00805f9b34fb"
    val BOND_MANAGEMENT_SERVICE = "0000181e-0000-1000-8000-00805f9b34fb"
    val GLUCOSE_SERVICE = "00001808-0000-1000-8000-00805f9b34fb"
    val RSC_SERVICE = "00001814-0000-1000-8000-00805f9b34fb"
    val BAROMETER_SERVICE = "00040001-0000-1000-8000-00805f9b0131"
    val ACCELEROMETER_SERVICE = "00040020-0000-1000-8000-00805f9b0131"
    val ANALOG_TEMPERATURE_SERVICE = "00040030-0000-1000-8000-00805f9b0131"
    val CSC_SERVICE = "00001816-0000-1000-8000-00805f9b34fb"
    val HUMAN_INTERFACE_DEVICE_SERVICE = "00001812-0000-1000-8000-00805f9b34fb"

    /**
     * Scan param characteristics
     */
    val SCAN_INTERVAL_WINDOW = "00002a4f-0000-1000-8000-00805f9b34fb"
    val SCAN_REFRESH = "00002a31-0000-1000-8000-00805f9b34fb"

    /**
     * Heart rate characteristics
     */
    val HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb"
    val BODY_SENSOR_LOCATION = "00002a38-0000-1000-8000-00805f9b34fb"
    /**
     * Device information characteristics
     */
    val SYSTEM_ID = "00002a23-0000-1000-8000-00805f9b34fb"
    val MODEL_NUMBER_STRING = "00002a24-0000-1000-8000-00805f9b34fb"
    val SERIAL_NUMBER_STRING = "00002a25-0000-1000-8000-00805f9b34fb"
    val FIRMWARE_REVISION_STRING = "00002a26-0000-1000-8000-00805f9b34fb"
    val HARDWARE_REVISION_STRING = "00002a27-0000-1000-8000-00805f9b34fb"
    val SOFTWARE_REVISION_STRING = "00002a28-0000-1000-8000-00805f9b34fb"
    val MANUFACTURER_NAME_STRING = "00002a29-0000-1000-8000-00805f9b34fb"
    val PNP_ID = "00002a50-0000-1000-8000-00805f9b34fb"
    val IEEE = "00002a2a-0000-1000-8000-00805f9b34fb"
    /**
     * Battery characteristics
     */
    val BATTERY_LEVEL = "00002a19-0000-1000-8000-00805f9b34fb"

    /**
     * Gatt services
     */
    val GENERIC_ACCESS_SERVICE = "00001800-0000-1000-8000-00805f9b34fb"
    val GENERIC_ATTRIBUTE_SERVICE = "00001801-0000-1000-8000-00805f9b34fb"
    /**
     * Find me characteristics
     */
    val ALERT_LEVEL = "00002a06-0000-1000-8000-00805f9b34fb"
    val TRANSMISSION_POWER_LEVEL = "00002a07-0000-1000-8000-00805f9b34fb"

    /**
     * Blood Pressure service Characteristics
     */
    val BLOOD_PRESSURE_MEASUREMENT = "00002a35-0000-1000-8000-00805f9b34fb"

    /*
    * Serial Port Profile
    * */
    val SERIAL_PORT = "fd5abba0-3935-11e5-85a6-0002a5d5c51b"
    val SERIAL_PORT_END_POINT = "fd5abba1-3935-11e5-85a6-0002a5d5c51b"
    val SERIAL_PORT_CREDITS = "fd5abba2-3935-11e5-85a6-0002a5d5c51b"


    /**
     * Descriptor UUID's
     */
    val CHARACTERISTIC_EXTENDED_PROPERTIES = "00002900-0000-1000-8000-00805f9b34fb"
    val CHARACTERISTIC_USER_DESCRIPTION = "00002901-0000-1000-8000-00805f9b34fb"
    val CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"
    val SERVER_CHARACTERISTIC_CONFIGURATION = "00002903-0000-1000-8000-00805f9b34fb"
    val CHARACTERISTIC_PRESENTATION_FORMAT = "00002904-0000-1000-8000-00805f9b34fb"
    val CHARACTERISTIC_AGGREGATE_FORMAT = "00002905-0000-1000-8000-00805f9b34fb"
    val VALID_RANGE = "00002906-0000-1000-8000-00805f9b34fb"
    val EXTERNAL_REPORT_REFERENCE = "00002907-0000-1000-8000-00805f9b34fb"
    val REPORT_REFERENCE = "00002908-0000-1000-8000-00805f9b34fb"
    val NUMBER_OF_DIGITALS = "00002909-0000-1000-8000-00805f9b34fb"
    val VALUE_TRIGGER_SETTINGS = "0000290A-0000-1000-8000-00805f9b34fb"
    val ENVIRONMENTAL_SENSING_CONFIGURATION = "0000290B-0000-1000-8000-00805f9b34fb"
    val ENVIRONMENTAL_SENSING_MEASUREMENT = "0000290C-0000-1000-8000-00805f9b34fb"
    val ENVIRONMENTAL_SENSING_TRIGGER_SETTING = "0000290D-0000-1000-8000-00805f9b34fb"
    val TIME_TRIGGER_SETTINGS = "0000290E-0000-1000-8000-00805f9b34fb"

    /*
    Characteristics UUID's
     */
    val HEART_RATE_CONTROL_POINT = "00002a39-0000-1000-8000-00805f9b34fb"
    val GLUCOSE_MEASUREMENT_CONTEXT = "00002a34-0000-1000-8000-00805f9b34fb"
    val GLUCOSE_MEASUREMENT = "00002a18-0000-1000-8000-00805f9b34fb"
    val GLUCOSE_FEATURE = "00002a51-0000-1000-8000-00805f9b34fb"
    val RECORD_ACCESS_CONTROL_POINT = "00002a52-0000-1000-8000-00805f9b34fb"
    val BLOOD_INTERMEDIATE_CUFF_PRESSURE = "00002a36-0000-1000-8000-00805f9b34fb"
    val BLOOD_PRESSURE_FEATURE = "00002a49-0000-1000-8000-00805f9b34fb"
    val RSC_FEATURE = "00002a54-0000-1000-8000-00805f9b34fb"
    val SC_SENSOR_LOCATION = "00002a5d-0000-1000-8000-00805f9b34fb"
    val SC_CONTROL_POINT = "00002a55-0000-1000-8000-00805f9b34fb"
    val CSC_FEATURE = "00002a5c-0000-1000-8000-00805f9b34fb"
    val RSC_MEASUREMENT = "00002a53-0000-1000-8000-00805f9b34fb"
    val CSC_MEASUREMENT = "00002a5b-0000-1000-8000-00805f9b34fb"
    val HEALTH_TEMP_MEASUREMENT = "00002a1c-0000-1000-8000-00805f9b34fb"
    val TEMPERATURE_TYPE = "00002a1d-0000-1000-8000-00805f9b34fb"
    /**
     * current time service
     */
    val CURRENT_TIME_SERVICE = "00001805-0000-1000-8000-00805f9b34fb"
    val NEXT_DST_CHANGE_SERVICE = "00001807-0000-1000-8000-00805f9b34fb"
    val REFERENCE_TIME_UPDATE_SERVICE = "00001806-0000-1000-8000-00805f9b34fb"
    val CURRENT_TIME = "00002A2B-0000-1000-8000-00805f9b34fb"
    val LOCAL_TIME = "00002A0F-0000-1000-8000-00805f9b34fb"
    val DST_TIME = "00002A11-0000-1000-8000-00805f9b34fb"
    val REFERENCE_TIME = "00002A14-0000-1000-8000-00805f9b34fb"
    val TIME_UPDATE_CONTROL_POINT = "00002A16-0000-1000-8000-00805f9b34fb"
    val TIME_UPDATE_STATE = "00002A17-0000-1000-8000-00805f9b34fb"
    /**
     * ANS
     */
    val ALERT_NOTIFICATION_SERVICE = "00001811-0000-1000-8000-00805f9b34fb"
    val ANS_SUPPORTED_NEW_ALERT_CATEGORY = "00002A47-0000-1000-8000-00805f9b34fb"
    val ANS_NEW_ALERT = "00002A46-0000-1000-8000-00805f9b34fb"
    val ANS_SUPPORTED_UNREAD_ALERT_CATEGORY = "00002A48-0000-1000-8000-00805f9b34fb"
    val ANS_UNREAD_ALERT_STATUS = "00002A45-0000-1000-8000-00805f9b34fb"
    val ANS_CONTROL_POINT = "00002A44-0000-1000-8000-00805f9b34fb"
    /**
     * Phone Alert characteristics
     *///                                                    00002902-0000-1000-8000-00805f9b34fb
    val PHONE_ALERT_STATUS_SERVICE = "0000180e-0000-1000-8000-00805f9b34fb"
    val PHONE_ALERT_STATUS = "00002a3f-0000-1000-8000-00805f9b34fb"
    val PHONE_ALERT_RINGER_SETTINGS = "00002a41-0000-1000-8000-00805f9b34fb"
    val PHONE_ALERT_CONTROL_POINT = "00002a40-0000-1000-8000-00805f9b34fb"
    /**
     * OTAU
     */
    val OTAU_SERVICE =
        "8b698d5b-04e1-48b1-9617-ac80aa64e0d0"//; //8b698d5b-04e1-48b1-9617-ac80aa64e0d0
    val OTAU_CHARACTERISTIC_1_INDICATE = "8b698d5b-04e1-48b1-9617-ac80aa64e0e0"
    val OTAU_CHARACTERISTIC_2_WRITE = "8b698d5b-04e1-48b1-9617-ac80aa64e0e5"
    val OTAU_CHARACTERISTIC_3_READ = "8b698d5b-04e1-48b1-9617-ac80aa64e0ea"
    /**
     * Eddystone characteristics
     */
    val EddyStoneConfigService = "ee0c2080-8786-40ba-ab96-99b91ac981d8"
    val EddyStoneLockStateCharacteristics = "ee0c2081-8786-40ba-ab96-99b91ac981d8"
    val EddyStoneLockCodeCharacteristics = "ee0c2082-8786-40ba-ab96-99b91ac981d8"
    val EddyStoneUnlockCharacteristics = "ee0c2083-8786-40ba-ab96-99b91ac981d8"
    val EddyStoneURICharacteristics = "ee0c2084-8786-40ba-ab96-99b91ac981d8"
    val EddyStoneFlagsCharacteristics = "ee0c2085-8786-40ba-ab96-99b91ac981d8"
    val EddyStoneTxPowerLevelCharacteristics = "ee0c2086-8786-40ba-ab96-99b91ac981d8"
    val EddyStoneTxModeCharacteristics = "ee0c2087-8786-40ba-ab96-99b91ac981d8"
    val EddyStoneBeaconPeriodCharacteristics = "ee0c2088-8786-40ba-ab96-99b91ac981d8"
    val EddyStoneResetCharacteristics = "ee0c2089-8786-40ba-ab96-99b91ac981d8"
    val HEALTH_THERMO_SERVICE = "00001809-0000-1000-8000-00805f9b34fb"
    val TEMPERATURE_INTERMEDIATE = "00002a1e-0000-1000-8000-00805f9b34fb"
    val TEMPERATURE_MEASUREMENT_INTERVAL = "00002a21-0000-1000-8000-00805f9b34fb"
    /**
     * Unused Service characteristics
     */
    val AEROBIC_HEART_RATE_LOWER_LIMIT = "00002a7e-0000-1000-8000-00805f9b34fb"
    val AEROBIC_HEART_RATE_UPPER_LIMIT = "00002a84-0000-1000-8000-00805f9b34fb"
    val AEROBIC_THRESHOLD = "00002a7f-0000-1000-8000-00805f9b34fb"
    val AGE = "00002a80-0000-1000-8000-00805f9b34fb"
    val ALERT_CATEGORY_ID = "00002a43-0000-1000-8000-00805f9b34fb"
    val ALERT_CATEGORY_ID_BIT_MASK = "00002a42-0000-1000-8000-00805f9b34fb"
    val ALERT_STATUS = "00002a3F-0000-1000-8000-00805f9b34fb"
    val ANAEROBIC_HEART_RATE_LOWER_LIMIT = "00002a81-0000-1000-8000-00805f9b34fb"
    val ANAEROBIC_HEART_RATE_UPPER_LIMIT = "00002a82-0000-1000-8000-00805f9b34fb"
    val ANAEROBIC_THRESHOLD = "00002aA83-0000-1000-8000-00805f9b34fb"
    val APPARENT_WIND_DIRECTION = "00002a73-0000-1000-8000-00805f9b34fb"
    val APPARENT_WIND_SPEED = "00002a72-0000-1000-8000-00805f9b34fb"
    val APPEARANCE = "00002a01-0000-1000-8000-00805f9b34fb"
    val BAROMETRIC_PRESSURE_TREND = "00002aa3-0000-1000-8000-00805f9b34fb"
    val BODY_COMPOSITION_FEATURE = "00002a9B-0000-1000-8000-00805f9b34fb"
    val BODY_COMPOSITION_MEASUREMENT = "00002a9C-0000-1000-8000-00805f9b34fb"
    val BOND_MANAGEMENT_CONTROL_POINT = "00002aa4-0000-1000-8000-00805f9b34fb"
    val BOND_MANAGEMENT_FEATURE = "00002aa5-0000-1000-8000-00805f9b34fb"
    val CENTRAL_ADDRESS_RESOLUTION = "00002aa6-0000-1000-8000-00805f9b34fb"
    val CGM_FEATURE = "00002aa8-0000-1000-8000-00805f9b34fb"
    val CGM_MEASUREMENT = "00002aa7-0000-1000-8000-00805f9b34fb"
    val CGM_SESSION_RUN_TIME = "00002aab-0000-1000-8000-00805f9b34fb"
    val CGM_SESSION_START_TIME = "00002aaa-0000-1000-8000-00805f9b34fb"
    val CGM_SPECIFIC_OPS_CONTROL_POINT = "00002aaC-0000-1000-8000-00805f9b34fb"
    val CGM_STATUS = "00002aa9-0000-1000-8000-00805f9b34fb"
    val CYCLING_POWER_CONTROL_POINT = "00002a66-0000-1000-8000-00805f9b34fb"
    val CYCLING_POWER_FEATURE = "00002a65-0000-1000-8000-00805f9b34fb"
    val CYCLING_POWER_MEASUREMENT = "00002a63-0000-1000-8000-00805f9b34fb"
    val CYCLING_POWER_VECTOR = "00002a64-0000-1000-8000-00805f9b34fb"
    val DATABASE_CHANGE_INCREMENT = "00002a99-0000-1000-8000-00805f9b34fb"
    val DATE_OF_BIRTH = "00002a85-0000-1000-8000-00805f9b0131"
    val DATE_OF_THRESHOLD_ASSESSMENT = "00002a86-0000-1000-8000-00805f9b0131"
    val DATE_TIME = "00002a08-0000-1000-8000-00805f9b34fb"
    val DAY_DATE_TIME = "00002a0a-0000-1000-8000-00805f9b34fb"
    val DAY_OF_WEEK = "00002A09-0000-1000-8000-00805f9b34fb"
    val DESCRIPTOR_VALUE_CHANGED = "00002a7d-0000-1000-8000-00805f9b34fb"
    val DEVICE_NAME = "00002a00-0000-1000-8000-00805f9b34fb"
    val DEW_POINT = "00002a7b-0000-1000-8000-00805f9b34fb"
    val DST_OFFSET = "00002a0d-0000-1000-8000-00805f9b34fb"
    val ELEVATION = "00002a6c-0000-1000-8000-00805f9b34fb"
    val EMAIL_ADDRESS = "00002a87-0000-1000-8000-00805f9b34fb"
    val EXACT_TIME_256 = "00002a0c-0000-1000-8000-00805f9b34fb"
    val FAT_BURN_HEART_RATE_LOWER_LIMIT = "00002a88-0000-1000-8000-00805f9b34fb"
    val FAT_BURN_HEART_RATE_UPPER_LIMIT = "00002a89-0000-1000-8000-00805f9b34fb"
    val FIRSTNAME = "00002a8a-0000-1000-8000-00805f9b34fb"
    val FIVE_ZONE_HEART_RATE_LIMITS = "00002A8b-0000-1000-8000-00805f9b34fb"
    val GENDER = "00002a8c-0000-1000-8000-00805f9b34fb"
    val GUST_FACTOR = "00002a74-0000-1000-8000-00805f9b34fb"
    val HEAT_INDEX = "00002a89-0000-1000-8000-00805f9b34fb"
    val HEIGHT = "00002a8a-0000-1000-8000-00805f9b34fb"
    val HEART_RATE_MAX = "00002a8d-0000-1000-8000-00805f9b34fb"
    val HIP_CIRCUMFERENCE = "00002a8f-0000-1000-8000-00805f9b34fb"
    val HUMIDITY = "00002a6f-0000-1000-8000-00805f9b34fb"
    val INTERMEDIATE_CUFF_PRESSURE = "00002a36-0000-1000-8000-00805f9b34fb"
    val INTERMEDIATE_TEMPERATURE = "00002a1e-0000-1000-8000-00805f9b34fb"
    val IRRADIANCE = "00002a77-0000-1000-8000-00805f9b34fb"
    val LANGUAGE = "00002aa2-0000-1000-8000-00805f9b34fb"
    val LAST_NAME = "00002a90-0000-1000-8000-00805f9b34fb"
    val LN_CONTROL_POINT = "00002a6b-0000-1000-8000-00805f9b34fb"
    val LN_FEATURE = "00002a6a-0000-1000-8000-00805f9b34fb"
    val LOCAL_TIME_INFORMATION = "00002a0f-0000-1000-8000-00805f9b34fb"
    val LOCATION_AND_SPEED = "00002a67-0000-1000-8000-00805f9b34fb"
    val MAGNETIC_DECLINATION = "00002a2c-0000-1000-8000-00805f9b34fb"
    val MAGNETIC_FLUX_DENSITY_2D = "00002aa0-0000-1000-8000-00805f9b34fb"
    val MAGNETIC_FLUX_DENSITY_3D = "00002aa1-0000-1000-8000-00805f9b34fb"
    val MANUFACTURE_NAME_STRING = "00002a29-0000-1000-8000-00805f9b34fb"
    val MAXIMUM_RECOMMENDED_HEART_RATE = "00002a91-0000-1000-8000-00805f9b34fb"
    val MEASUREMENT_INTERVAL = "00002a21-0000-1000-8000-00805f9b34fb"
    val NAVIGATION = "00002a68-0000-1000-8000-00805f9b34fb"
    val NEW_ALERT = "00002a46-0000-1000-8000-00805f9b34fb"
    val PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS = "00002a04-0000-1000-8000-00805f9b34fb"
    val PERIPHERAL_PRIVACY_FLAG = "00002a02-0000-1000-8000-00805f9b34fb"
    val POLLEN_CONCENTRATION = "00002a75-0000-1000-8000-00805f9b34fb"
    val POSITION_QUALITY = "00002a69-0000-1000-8000-00805f9b34fb"
    val PRESSURE = "00002a6d-0000-1000-8000-00805f9b34fb"
    val RAINFALL = "00002a78-0000-1000-8000-00805f9b34fb"
    val RECONNECTION_ADDRESS = "00002a03-0000-1000-8000-00805f9b34fb"
    val REFERNCE_TIME_INFORMATION = "00002a14-0000-1000-8000-00805f9b34fb"
    val RESTING_HEART_RATE = "00002a92-0000-1000-8000-00805f9b34fb"
    val RINGER_CONTROL_POINT = "00002a40-0000-1000-8000-00805f9b34fb"
    val RINGER_SETTING = "00002a41-0000-1000-8000-00805f9b34fb"
    val SENSOR_LOCATION = "00002a5d-0000-1000-8000-00805f9b34fb"
    val SERVICE_CHANGED = "00002a05-0000-1000-8000-00805f9b34fb"
    val SPORT_TYPE_FOR_AEROBIC_AND_ANAEROBIC_THRESHOLDS = "00002a93-0000-1000-8000-00805f9b34fb"
    val SUPPORTED_NEW_ALERT_CATEGORY = "00002a47-0000-1000-8000-00805f9b34fb"
    val SUPPORTED_UNREAD_ALERT_CATEGORY = "00002a48-0000-1000-8000-00805f9b34fb"
    val TEMPERATURE = "00002a6e-0000-1000-8000-00805f9b34fb"
    val TEMPERATURE_MEASUREMENT = "00002a1c-0000-1000-8000-00805f9b34fb"
    val THREE_ZONE_HEART_RATE_LIMITS = "00002a94-0000-1000-8000-00805f9b34fb"
    val TIME_ACCURACY = "00002a12-0000-1000-8000-00805f9b34fb"
    val TIME_SOURCE = "00002a13-0000-1000-8000-00805f9b34fb"
    val TIME_WITH_DST = "00002a11-0000-1000-8000-00805f9b34fb"
    val TIME_ZONE = "00002a0e-0000-1000-8000-00805f9b34fb"
    val TRUE_WIND_DIRECTION = "00002a71-0000-1000-8000-00805f9b34fb"
    val TRUE_WIND_SPEED = "00002a70-0000-1000-8000-00805f9b34fb"
    val TWO_ZONE_HEART_RATE = "00002a95-0000-1000-8000-00805f9b34fb"
    val TX_POWER = "00002a07-0000-1000-8000-00805f9b34fb"
    val UNCERTAINITY = "00002ab4-0000-1000-8000-00805f9b34fb"
    val UNREAD_ALERT_STATUS = "00002a45-0000-1000-8000-00805f9b34fb"
    val USER_CONTROL_POINT = "00002a9f-0000-1000-8000-00805f9b34fb"
    val USER_INDEX = "00002a9a-0000-1000-8000-00805f9b34fb"
    val UV_INDEX = "00002a76-0000-1000-8000-00805f9b34fb"
    val VO2_MAX = "00002a96-0000-1000-8000-00805f9b34fb"
    val WAIST_CIRCUMFERENCE = "00002a97-0000-1000-8000-00805f9b34fb"
    val WEIGHT = "00002a98-0000-1000-8000-00805f9b34fb"
    val WEIGHT_SCALE_FEATURE = "00002a9e-0000-1000-8000-00805f9b34fb"
    val WIND_CHILL = "00002a7-0000-1000-8000-00805f9b34fb"

    val attributes = mapOf<String, String>(

        // Services.
        HEART_RATE_SERVICE to "Heart Rate Service",
        HEALTH_THERMO_SERVICE to "Health Thermometer Service",
        GENERIC_ACCESS_SERVICE to "Generic Access Service",
        GENERIC_ATTRIBUTE_SERVICE to "Generic Attribute Service",
        DEVICE_INFORMATION_SERVICE to "Device Information Service",
        BATTERY_SERVICE to "Battery Service",
        IMMEDIATE_ALERT_SERVICE to "Immediate Alert",
        LINK_LOSS_SERVICE to "Link Loss",
        TRANSMISSION_POWER_SERVICE to "Tx Power",
        BLOOD_PRESSURE_SERVICE to "Blood Pressure Service",
        SERIAL_PORT to "Custom Serial Chat",
        SERIAL_PORT_END_POINT to "Custom Serial Chat",

        // Unused Services
        ALERT_NOTIFICATION_SERVICE to "Alert Notification Service",
        PHONE_ALERT_STATUS_SERVICE to "Phone Alert Status",
        CURRENT_TIME_SERVICE to "Current Time Service",
        NEXT_DST_CHANGE_SERVICE to "Next DST Change Service",
        PHONE_ALERT_STATUS_SERVICE to "Phone Alert Status Service",
        REFERENCE_TIME_UPDATE_SERVICE to "Reference Time Update Service",
        SCAN_PARAMETERS_SERVICE to "Scan Parameter service",

        // Heart Rate Characteristics.
        HEART_RATE_MEASUREMENT to "Heart Rate Measurement",
        BODY_SENSOR_LOCATION to "Body Sensor Location",
        HEART_RATE_CONTROL_POINT to "Heart Rate Control Point",

        // Health thermometer Characteristics.
        HEALTH_TEMP_MEASUREMENT to "Health Thermometer Measurement",
        TEMPERATURE_TYPE to "Temperature Type",
        TEMPERATURE_INTERMEDIATE to "Intermediate Temperature",
        TEMPERATURE_MEASUREMENT_INTERVAL to "Measurement Interval",

        // Device Information Characteristics
        SYSTEM_ID to "System ID",
        MODEL_NUMBER_STRING to "Model Number String",
        SERIAL_NUMBER_STRING to "Serial Number String",
        FIRMWARE_REVISION_STRING to "Firmware Revision String",
        HARDWARE_REVISION_STRING to "Hardware Revision String",
        SOFTWARE_REVISION_STRING to "Software Revision String",
        MANUFACTURER_NAME_STRING to "Manufacturer Name String",
        PNP_ID to "PnP ID",
        IEEE to "IEEE 11073-20601 Regulatory Certification Data List",

        // Battery service characteristics
        BATTERY_LEVEL to "Battery Level",

        // Find me service characteristics
        ALERT_LEVEL to "Alert Level",
        TRANSMISSION_POWER_LEVEL to "Tx Power Level",

        // Blood pressure service characteristics
        BLOOD_INTERMEDIATE_CUFF_PRESSURE to "Intermediate Cuff Pressure",
        BLOOD_PRESSURE_FEATURE to "Blood Pressure Feature",
        BLOOD_PRESSURE_MEASUREMENT to "Blood Pressure Measurement",

        // Unused Services
        ALERT_NOTIFICATION_SERVICE to "Alert notification Service",
        BODY_COMPOSITION_SERVICE to "Body Composition Service",
        BOND_MANAGEMENT_SERVICE to "Bond Management Service",
        CONTINUOUS_GLUCOSE_MONITORING_SERVICE to "Continuous Glucose Monitoring Service",
        CURRENT_TIME_SERVICE to "Current Time Service",
        CYCLING_POWER_SERVICE to "Cycling Power Service",
        ENVIRONMENTAL_SENSING_SERVICE to "Environmental Sensing Service",
        GLUCOSE_SERVICE to "Glucose",
        HUMAN_INTERFACE_DEVICE_SERVICE to "Human Interface Device Service",
        LOCATION_NAVIGATION_SERVICE to "Location and Navigation Service",
        NEXT_DST_CHANGE_SERVICE to "Next DST Change Service",
        PHONE_ALERT_STATUS_SERVICE to "Phone Alert Status Service",
        REFERENCE_TIME_UPDATE_SERVICE to "Reference Time Update Service",
        SCAN_PARAMETERS_SERVICE to "Scan Paramenters Service",
        USER_DATA_SERVICE to "User Data",
        WEIGHT_SCALE_SERVICE to "Weight Scale",
        AUTOMATION_IO_SERVICE to "Automation IO",
        CONTINUOUS_TIME_SERVICE to "Continuous Time Service",
        HTTP_PROXY_SERVICE to "HTTP Proxy",
        INDOOR_POSITIONING_SERVICE to "Indoor Positioning",
        INTERNET_PROTOCOL_SERVICE to "Internet Protocol Support",
        PULSE_OXIMETER_SERVICE to "Pulse Oximeter",
        TRANSPORT_DISCOVERY to "Transport Discovery",
        RSC_SERVICE to "Running Speed and Cadence",

        // Heart Rate Characteristics.
        HEART_RATE_MEASUREMENT to "Heart Rate Measurement",
        BODY_SENSOR_LOCATION to "Body Sensor Location",
        HEART_RATE_CONTROL_POINT to "Heart Rate Control Point",

        // Health thermometer Characteristics.
        HEALTH_TEMP_MEASUREMENT to "Health Thermometer Measurement",
        TEMPERATURE_TYPE to "Temperature Type",
        TEMPERATURE_INTERMEDIATE to "Intermediate Temperature",
        TEMPERATURE_MEASUREMENT_INTERVAL to "Measurement Interval",

        // Device Information Characteristics
        SYSTEM_ID to "System ID",
        MODEL_NUMBER_STRING to "Model Number String",
        SERIAL_NUMBER_STRING to "Serial Number String",
        FIRMWARE_REVISION_STRING to "Firmware Revision String",
        HARDWARE_REVISION_STRING to "Hardware Revision String",
        SOFTWARE_REVISION_STRING to "Software Revision String",
        MANUFACTURE_NAME_STRING to "Manufacturer Name String",
        PNP_ID to "PnP ID",
        IEEE to "IEEE 11073-20601 Regulatory Certification Data List",

        // Battery service characteristics
        BATTERY_LEVEL to "Battery Level",

        // Find me service characteristics
        ALERT_LEVEL to "Alert Level",
        TRANSMISSION_POWER_LEVEL to "Tx Power Level",

        // Glucose Characteristics
        GLUCOSE_MEASUREMENT to "Glucose Measurement",
        GLUCOSE_MEASUREMENT_CONTEXT to "Glucose Measurement Context",
        GLUCOSE_FEATURE to "Glucose Feature",
        RECORD_ACCESS_CONTROL_POINT to "Record Access Control Point",

        // Blood pressure service characteristics
        BLOOD_INTERMEDIATE_CUFF_PRESSURE to "Intermediate Cuff Pressure",
        BLOOD_PRESSURE_FEATURE to "Blood Pressure Feature",
        BLOOD_PRESSURE_MEASUREMENT to "Blood Pressure Measurement",

        // Running Speed Characteristics
        RSC_MEASUREMENT to "Running Speed and Cadence Measurement",
        RSC_FEATURE to "Running Speed and Cadence Feature",
        SC_CONTROL_POINT to "Speed and Cadence Control Point",
        SC_SENSOR_LOCATION to "Speed and Cadence Sensor Location",

        // Cycling Speed Characteristics
        CSC_SERVICE to "Cycling Speed and Cadence",
        CSC_MEASUREMENT to "Cycling Speed and Cadence Measurement",
        CSC_FEATURE to "Cycling Speed and Cadence Feature",

        //OTAU Characteristics
        OTAU_SERVICE to "OTAU Service",
        OTAU_CHARACTERISTIC_1_INDICATE to "OTAU Characteristic Indicate",
        OTAU_CHARACTERISTIC_2_WRITE to "OTAU Characteristic Write",
        OTAU_CHARACTERISTIC_3_READ to "OTAU Characteristic Read",

        // Unused Characteristics
        AEROBIC_HEART_RATE_LOWER_LIMIT to "Aerobic Heart Rate Lower Limit",
        AEROBIC_HEART_RATE_UPPER_LIMIT to "Aerobic Heart Rate Upper Limit",
        AGE to "Age",
        ALERT_CATEGORY_ID to "Alert Category Id",
        ALERT_CATEGORY_ID_BIT_MASK to "Alert Category_id_Bit_Mask",
        ALERT_STATUS to "Alert_Status",
        ANAEROBIC_HEART_RATE_LOWER_LIMIT to "Anaerobic Heart Rate Lower Limit",
        ANAEROBIC_HEART_RATE_UPPER_LIMIT to "Anaerobic Heart Rate Upper Limit",
        ANAEROBIC_THRESHOLD to "Anaerobic Threshold",
        APPARENT_WIND_DIRECTION to "Apparent Wind Direction",
        APPARENT_WIND_SPEED to "Apparent Wind Speed",
        APPEARANCE to "Appearance",
        BAROMETRIC_PRESSURE_TREND to "Barometric pressure Trend",
        BLOOD_PRESSURE_MEASUREMENT to "Blood Pressure Measurement",
        BODY_COMPOSITION_FEATURE to "Body Composition Feature",
        BODY_COMPOSITION_MEASUREMENT to "Body Composition Measurement",
        BOND_MANAGEMENT_CONTROL_POINT to "Bond Management Control Point",
        BOND_MANAGEMENT_FEATURE to "Bond Management feature",
        CGM_FEATURE to "CGM Feature",
        CENTRAL_ADDRESS_RESOLUTION to "Central Address Resolution",
        FIRSTNAME to "First Name",
        GUST_FACTOR to "Gust Factor",
        CGM_MEASUREMENT to "CGM Measurement",
        CGM_SESSION_RUN_TIME to "CGM Session Run Time",
        CGM_SESSION_START_TIME to "CGM Session Start Time",
        CGM_SPECIFIC_OPS_CONTROL_POINT to "CGM Specific Ops Control Point",
        CGM_STATUS to "CGM Status",
        CYCLING_POWER_CONTROL_POINT to "Cycling Power Control Point",
        CYCLING_POWER_VECTOR to "Cycling Power Vector",
        CYCLING_POWER_FEATURE to "Cycling Power Feature",
        CYCLING_POWER_MEASUREMENT to "Cycling Power Measurement",
        DATABASE_CHANGE_INCREMENT to "Database Change Increment",
        DATE_OF_BIRTH to "Date Of Birth",
        DATE_OF_THRESHOLD_ASSESSMENT to "Date Of Threshold Assessment",
        DATE_TIME to "Date Time",
        DAY_DATE_TIME to "Day Date Time",
        DAY_OF_WEEK to "Day Of Week",
        DESCRIPTOR_VALUE_CHANGED to "Descriptor Value Changed",
        DEVICE_NAME to "Device Name",
        DEW_POINT to "Dew Point",
        DST_OFFSET to "DST Offset",
        ELEVATION to "Elevation",
        EMAIL_ADDRESS to "Email Address",
        EXACT_TIME_256 to "Exact Time 256",
        FAT_BURN_HEART_RATE_LOWER_LIMIT to "Fat Burn Heart Rate lower Limit",
        FAT_BURN_HEART_RATE_UPPER_LIMIT to "Fat Burn Heart Rate Upper Limit",
        FIRMWARE_REVISION_STRING to "Firmware Revision String",
        FIVE_ZONE_HEART_RATE_LIMITS to "Five Zone Heart Rate Limits",
        MANUFACTURE_NAME_STRING to "Manufacturer Name String",
        GENDER to "Gender",
        GLUCOSE_FEATURE to "Glucose Feature",
        GLUCOSE_MEASUREMENT to "Glucose Measurement",
        HEART_RATE_MAX to "Heart Rate Max",
        HEAT_INDEX to "Heat Index",
        HEIGHT to "Height",
        HIP_CIRCUMFERENCE to "Hip Circumference",
        HUMIDITY to "Humidity",
        INTERMEDIATE_CUFF_PRESSURE to "Intermediate Cuff Pressure",
        INTERMEDIATE_TEMPERATURE to "Intermediate Temperature",
        IRRADIANCE to "Irradiance",
        LANGUAGE to "Language",
        LAST_NAME to "Last Name",
        LN_CONTROL_POINT to "LN Control Point",
        LN_FEATURE to "LN Feature",
        LOCAL_TIME_INFORMATION to "Local Time Information",
        LOCATION_AND_SPEED to "Location and Speed",
        MAGNETIC_DECLINATION to "Magenetic Declination",
        MAGNETIC_FLUX_DENSITY_2D to "Magentic Flux Density 2D",
        MAGNETIC_FLUX_DENSITY_3D to "Magentic Flux Density 3D",
        MAXIMUM_RECOMMENDED_HEART_RATE to "Maximum Recommended Heart Rate",
        MEASUREMENT_INTERVAL to "Measurement Interval",
        MODEL_NUMBER_STRING to "Model Number String",
        NEW_ALERT to "New Alert",
        NAVIGATION to "Navigation",
        PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS to "Peripheral Preferred Connection Parameters",
        PERIPHERAL_PRIVACY_FLAG to "Peripheral Privacy Flag",
        POLLEN_CONCENTRATION to "Pollen Concentration",
        POSITION_QUALITY to "Position Quality",
        PRESSURE to "Pressure",
        SERVICE_CHANGED to "Service Changed",
        RAINFALL to "Rainfall",

        // Descriptors
        CHARACTERISTIC_EXTENDED_PROPERTIES to "Characteristic Extended Properties",
        CHARACTERISTIC_USER_DESCRIPTION to "Characteristic User Description",
        CLIENT_CHARACTERISTIC_CONFIG to "Client Characteristic Configuration",
        SERVER_CHARACTERISTIC_CONFIGURATION to "Server Characteristic Configuration",
        CHARACTERISTIC_PRESENTATION_FORMAT to "Characteristic Presentation Format",
        CHARACTERISTIC_AGGREGATE_FORMAT to "Characteristic Aggregate Format",
        VALID_RANGE to "Valid Range",
        EXTERNAL_REPORT_REFERENCE to "External Report Reference",
        REPORT_REFERENCE to "Report Reference",
        NUMBER_OF_DIGITALS to "Number of Digitals",
        VALUE_TRIGGER_SETTINGS to "Value Trigger Settings",
        TIME_TRIGGER_SETTINGS to "Time Trigger Settings",
        ENVIRONMENTAL_SENSING_CONFIGURATION to "Environmental Sensing Configuration",
        ENVIRONMENTAL_SENSING_MEASUREMENT to "Environmental Sensing Measurement",
        ENVIRONMENTAL_SENSING_TRIGGER_SETTING to "Environmental Sensing Trigger Setting"

    )

    val descriptorAttributes = mapOf(

        /**
         * Descriptor key value mapping
         */
        "0" to "Reserved For Future Use",
        "1" to "Boolean",
        "2" to "unsigned 2-bit integer",
        "3" to "unsigned 4-bit integer",
        "4" to "unsigned 8-bit integer",
        "5" to "unsigned 12-bit integer",
        "6" to "unsigned 16-bit integer",
        "7" to "unsigned 24-bit integer",
        "8" to "unsigned 32-bit integer",
        "9" to "unsigned 48-bit integer",
        "10" to "unsigned 64-bit integer",
        "11" to "unsigned 128-bit integer",
        "12" to "signed 8-bit integer",
        "13" to "signed 12-bit integer",
        "14" to "signed 16-bit integer",
        "15" to "signed 24-bit integer",
        "16" to "signed 32-bit integer",
        "17" to "signed 48-bit integer",
        "18" to "signed 64-bit integer",
        "19" to "signed 128-bit integer",
        "20" to "IEEE-754 32-bit floating point",
        "21" to "IEEE-754 64-bit floating point",
        "22" to "IEEE-11073 16-bit SFLOAT",
        "23" to "IEEE-11073 32-bit FLOAT",
        "24" to "IEEE-20601 format",
        "25" to "UTF-8 string",
        "26" to "UTF-16 string",
        "27" to "Opaque Structure"

    )


    fun lookup(uuid: String, defaultName: String = "Unknown"): String {
        val name = attributes[uuid]
        return name ?: defaultName
    }

    fun lookCharacteristicPresentationFormat(key: String): String {
        val value = descriptorAttributes[key]
        return value ?: "Reserved"
    }


    fun lookupreqHRMCharacateristics(uuid: String): Boolean {
        val name = attributes[uuid]
        return name != null

    }

    fun getname(uuid: String): String {
        val name = attributes[uuid]
        return name ?: "Not found"
    }
}