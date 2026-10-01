package com.example.data

data class MasjidTranslation(
    val nameUr: String,
    val nameHi: String,
    val areaUr: String,
    val areaHi: String,
    val cityUr: String,
    val cityHi: String
)

val masjidTranslations: Map<String, MasjidTranslation> = mapOf(
    // Google Sheet Masajid (Solapur)
    "100111111" to MasjidTranslation(
        nameUr = "محمدیہ مسجد",
        nameHi = "मोहम्मदिया मस्जिद",
        areaUr = "سواگت نگر",
        areaHi = "स्वागत नगर",
        cityUr = "سولاپور",
        cityHi = "सोलापूर"
    ),
    "100111112" to MasjidTranslation(
        nameUr = "حضرت امام حسین مسجد",
        nameHi = "हज़रत इमाम हुसैन मस्जिद",
        areaUr = "تائی چوک",
        areaHi = "ताई चौक",
        cityUr = "سولاپور",
        cityHi = "सोलापूर"
    ),

    // Delhi
    "delhi_jama_masjid" to MasjidTranslation(
        nameUr = "جامع مسجد",
        nameHi = "जामा मस्जिद",
        areaUr = "پرانی دہلی",
        areaHi = "पुरानी दिल्ली",
        cityUr = "دہلی",
        cityHi = "दिल्ली"
    ),
    "delhi_fatehpuri" to MasjidTranslation(
        nameUr = "فتح پوری مسجد",
        nameHi = "फतेहपुरी मस्जिद",
        areaUr = "چاندنی چوک",
        areaHi = "चाँदनी चौक",
        cityUr = "دہلی",
        cityHi = "दिल्ली"
    ),
    "delhi_nizamuddin" to MasjidTranslation(
        nameUr = "درگاہ نظام الدین اولیاء مسجد",
        nameHi = "दरगाह निज़ामुद्दीन औलिया मस्जिद",
        areaUr = "نظام الدین ویسٹ",
        areaHi = "निज़ामुद्दीन वेस्ट",
        cityUr = "دہلی",
        cityHi = "दिल्ली"
    ),
    "delhi_sunehri" to MasjidTranslation(
        nameUr = "سنہری مسجد",
        nameHi = "सुनेहरी मस्जिद",
        areaUr = "چاندنی چوک",
        areaHi = "चाँदनी चौक",
        cityUr = "دہلی",
        cityHi = "दिल्ली"
    ),
    "delhi_jamia" to MasjidTranslation(
        nameUr = "سینٹرل جامع مسجد",
        nameHi = "सेंट्रल जामिया मस्जिद",
        areaUr = "جامعہ ملیہ اسلامیہ",
        areaHi = "जामिया मिलिया इस्लामिया",
        cityUr = "دہلی",
        cityHi = "दिल्ली"
    ),
    "delhi_zeenat" to MasjidTranslation(
        nameUr = "زینت المساجد (گھٹا مسجد)",
        nameHi = "ज़ीनत-उल-मसाजिद (घटा मस्जिद)",
        areaUr = "دریا گنج",
        areaHi = "दरियागंज",
        cityUr = "دہلی",
        cityHi = "दिल्ली"
    ),
    "delhi_quba" to MasjidTranslation(
        nameUr = "مسجد قباء",
        nameHi = "मस्जिद-ए-क़ुबा",
        areaUr = "ابوالفضل اینکلیو",
        areaHi = "अबुल फ़ज़ल एन्क्लेव",
        cityUr = "دہلی",
        cityHi = "दिल्ली"
    ),
    "delhi_madina" to MasjidTranslation(
        nameUr = "مدینہ مسجد",
        nameHi = "मदीना मस्जिद",
        areaUr = "بتلہ ہاؤس، اوکھلا",
        areaHi = "बटला हाउस, ओखला",
        cityUr = "دہلی",
        cityHi = "दिल्ली"
    ),
    "delhi_bilal" to MasjidTranslation(
        nameUr = "مسجد بلال",
        nameHi = "मस्जिद-ए-बिलाل",
        areaUr = "سیلم پور",
        areaHi = "सीलमपुर",
        cityUr = "دہلی",
        cityHi = "दिल्ली"
    ),
    "delhi_khirki" to MasjidTranslation(
        nameUr = "کھڑکی مسجد",
        nameHi = "खिड़की मस्जिद",
        areaUr = "مالویہ نگر",
        areaHi = "मालवीय नगर",
        cityUr = "دہلی",
        cityHi = "दिल्ली"
    ),
    "delhi_kalan" to MasjidTranslation(
        nameUr = "کلاں مسجد",
        nameHi = "कलां मस्जिद",
        areaUr = "ترکمان گیٹ",
        areaHi = "तुर्कमान गेट",
        cityUr = "دہلی",
        cityHi = "दिल्ली"
    ),
    "delhi_moth" to MasjidTranslation(
        nameUr = "موٹھ کی مسجد",
        nameHi = "मोठ की मस्जिद",
        areaUr = "ساؤتھ ایکسٹینشن",
        areaHi = "साउथ एक्सटेंशन",
        cityUr = "دہلی",
        cityHi = "दिल्ली"
    ),

    // Mumbai & Maharashtra
    "mumbai_haji_ali" to MasjidTranslation(
        nameUr = "حاجی علی درگاہ و مسجد",
        nameHi = "हाजी अली दरगाह और मस्जिद",
        areaUr = "ورلی",
        areaHi = "वर्ली",
        cityUr = "ممبئی",
        cityHi = "मुंबई"
    ),
    "mumbai_minara" to MasjidTranslation(
        nameUr = "مینارہ مسجد",
        nameHi = "मीनारा मस्जिद",
        areaUr = "محمد علی روڈ",
        areaHi = "मोहम्मद अली रोड",
        cityUr = "ممبئی",
        cityHi = "मुंबई"
    ),
    "mumbai_jama" to MasjidTranslation(
        nameUr = "جامع مسجد ممبئی",
        nameHi = "जामा मस्जिद मुंबई",
        areaUr = "کرافورڈ مارکیٹ",
        areaHi = "क्रॉफर्ड मार्केट",
        cityUr = "ممبئی",
        cityHi = "मुंबई"
    ),
    "mumbai_mahim" to MasjidTranslation(
        nameUr = "مخدوم ماہی می شاہی مسجد",
        nameHi = "मख़दूम माहिमी शाही मस्जिद",
        areaUr = "ماہم ویسٹ",
        areaHi = "माहिम वेस्ट",
        cityUr = "ممبئی",
        cityHi = "मुंबई"
    ),
    "mumbai_zakaria" to MasjidTranslation(
        nameUr = "زکریا مسجد",
        nameHi = "ज़कारिया मस्जिद",
        areaUr = "مانڈوی",
        areaHi = "मांडवी",
        cityUr = "ممبئی",
        cityHi = "मुंबई"
    ),
    "mumbai_anjuman" to MasjidTranslation(
        nameUr = "انجمن اسلام مرکزی مسجد",
        nameHi = "अंजुमन-ए-इस्लाम सेंट्रल मस्जिद",
        areaUr = "سی ایس ٹی",
        areaHi = "सीएसटी",
        cityUr = "ممبئی",
        cityHi = "मुंबई"
    ),
    "mumbai_bandra" to MasjidTranslation(
        nameUr = "باندرہ جامع مسجد",
        nameHi = "बांद्रा जामा मस्जिद",
        areaUr = "باندرہ ویسٹ",
        areaHi = "बांद्रा वेस्ट",
        cityUr = "ممبئی",
        cityHi = "मुंबई"
    ),
    "mumbai_kurla" to MasjidTranslation(
        nameUr = "مرکز مسجد کرلا",
        nameHi = "मरकज़ मस्जिद कुर्ला",
        areaUr = "کرلا ویسٹ",
        areaHi = "कुर्ला वेस्ट",
        cityUr = "ممبئی",
        cityHi = "मुंबई"
    ),
    "mumbai_andheri" to MasjidTranslation(
        nameUr = "اندھیری جامع مسجد",
        nameHi = "अंधेरी जामा मस्जिद",
        areaUr = "ایس وی روڈ، اندھیری",
        areaHi = "एस.वी. रोड, अंधेरी",
        cityUr = "ممبئی",
        cityHi = "मुंबई"
    ),
    "mumbai_mominpura" to MasjidTranslation(
        nameUr = "مومن پورہ بڑی مسجد",
        nameHi = "मोमिनपुरा बड़ी मस्जिद",
        areaUr = "بھائیکلہ",
        areaHi = "भायखला",
        cityUr = "ممبئی",
        cityHi = "मुंबई"
    ),
    "mumbai_govandi" to MasjidTranslation(
        nameUr = "دیونار جامع مسجد",
        nameHi = "देवनार जामा मस्जिद",
        areaUr = "گوونڈی",
        areaHi = "गोवंडी",
        cityUr = "ممبئی",
        cityHi = "मुंबई"
    ),
    "mumbai_jogeshwari" to MasjidTranslation(
        nameUr = "ملت نگر جامع مسجد",
        nameHi = "मिल्लत नगर जामा मस्जिद",
        areaUr = "جوگیشوری ویسٹ",
        areaHi = "जोगेश्वरी वेस्ट",
        cityUr = "ممبئی",
        cityHi = "मुंबई"
    ),
    "pune_chand_tara" to MasjidTranslation(
        nameUr = "چاند تارا مسجد",
        nameHi = "चाँद तारा मस्जिद",
        areaUr = "نانا پیٹھ",
        areaHi = "नाना पेठ",
        cityUr = "پونے",
        cityHi = "पुणे"
    ),
    "pune_camp" to MasjidTranslation(
        nameUr = "کیمپ جامع مسجد",
        nameHi = "कैंप जामा मस्जिद",
        areaUr = "کینٹونمنٹ",
        areaHi = "छावनी",
        cityUr = "پونے",
        cityHi = "पुणे"
    ),
    "pune_kausar" to MasjidTranslation(
        nameUr = "کوثر باغ مسجد",
        nameHi = "कौसर बाग़ मस्जिद",
        areaUr = "کونڈھوا",
        areaHi = "कोंढवा",
        cityUr = "پونے",
        cityHi = "पुणे"
    ),
    "aurangabad_killa" to MasjidTranslation(
        nameUr = "جامع مسجد قلعہ ارک",
        nameHi = "जामा मस्जिद क़िला अर्राक",
        areaUr = "پرانا شہر",
        areaHi = "पुराना शहर",
        cityUr = "اورنگ آباد",
        cityHi = "औरंगाबाद"
    ),
    "aurangabad_kali" to MasjidTranslation(
        nameUr = "کالی مسجد",
        nameHi = "काली मस्जिद",
        areaUr = "جونا بازار",
        areaHi = "जूना बाज़ार",
        cityUr = "اورنگ آباد",
        cityHi = "औरंगाबाद"
    ),
    "nagpur_jama" to MasjidTranslation(
        nameUr = "بڑی جامع مسجد مومن پورہ",
        nameHi = "बड़ी जामा मस्जिद मोमिनपुरा",
        areaUr = "مومن پورہ",
        areaHi = "मोमिनपुरा",
        cityUr = "ناگپور",
        cityHi = "नागपुर"
    ),
    "nashik_jama" to MasjidTranslation(
        nameUr = "جامع مسجد دودھ بازار",
        nameHi = "जामा मस्जिद दूध बाज़ार",
        areaUr = "پرانا ناسک",
        areaHi = "पुराना नासिक",
        cityUr = "ناسک",
        cityHi = "नासिक"
    ),
    "thane_jama" to MasjidTranslation(
        nameUr = "تھانے جامع مسجد",
        nameHi = "ठाणे जामा मस्जिद",
        areaUr = "اسٹیشن روڈ",
        areaHi = "स्टेशन रोड",
        cityUr = "تھانے",
        cityHi = "ठाणे"
    ),

    // Hyderabad & Telangana
    "hyd_makkah" to MasjidTranslation(
        nameUr = "مکہ مسجد",
        nameHi = "मक्का मस्जिद",
        areaUr = "چارمینار",
        areaHi = "चारमीनार",
        cityUr = "حیدرآباد",
        cityHi = "हैदराबाद"
    ),
    "hyd_shahi" to MasjidTranslation(
        nameUr = "شاہی مسجد پبلک گارڈنز",
        nameHi = "शाही मस्जिद पब्लिक गार्डन्स",
        areaUr = "نامپلی",
        areaHi = "नामपल्ली",
        cityUr = "حیدرآباد",
        cityHi = "हैदराबाद"
    ),
    "hyd_toli" to MasjidTranslation(
        nameUr = "تولی مسجد",
        nameHi = "टोली मस्जिद",
        areaUr = "کاروان",
        areaHi = "कारवान",
        cityUr = "حیدرآباد",
        cityHi = "हैदराबाद"
    ),
    "hyd_spanish" to MasjidTranslation(
        nameUr = "ہسپانوی مسجد (بیگم پیٹ)",
        nameHi = "स्पैनिश मस्जिद (बेगमपेट)",
        areaUr = "بیگم پیٹ",
        areaHi = "बेगमपेट",
        cityUr = "حیدرآباد",
        cityHi = "हैदराबाद"
    ),
    "hyd_azizia" to MasjidTranslation(
        nameUr = "مسجد عزیزیہ",
        nameHi = "मस्जिद-ए-अज़ीज़िया",
        areaUr = "ہمایوں نگر",
        areaHi = "हुमायूँ नगर",
        cityUr = "حیدرآباد",
        cityHi = "हैदराबाद"
    ),
    "hyd_kulsumpura" to MasjidTranslation(
        nameUr = "کلثوم پورہ جامع مسجد",
        nameHi = "कुलसुमपुरा जामा मस्जिद",
        areaUr = "کاروان ساہو",
        areaHi = "कारवान साहू",
        cityUr = "حیدرآباد",
        cityHi = "हैदराबाद"
    ),
    "hyd_begumpet" to MasjidTranslation(
        nameUr = "بیگم پیٹ مرکزی مسجد",
        nameHi = "बेगमपेट सेंट्रल मस्जिद",
        areaUr = "سکندرآباد",
        areaHi = "सिकंदराबाद",
        cityUr = "حیدرآباد",
        cityHi = "हैदराबाद"
    ),
    "hyd_hayat" to MasjidTranslation(
        nameUr = "حیات بخشی مسجد",
        nameHi = "हयात बख़्शी मस्जिद",
        areaUr = "حیات نگر",
        areaHi = "हयात नगर",
        cityUr = "حیدرآباد",
        cityHi = "हैदराबाद"
    ),
    "hyd_musheerabad" to MasjidTranslation(
        nameUr = "مشیرآباد بڑی مسجد",
        nameHi = "मुशीराबाद बड़ी मस्जिद",
        areaUr = "مشیرآباد",
        areaHi = "मुशीराबाद",
        cityUr = "حیدرآباد",
        cityHi = "हैदराबाद"
    ),
    "hyd_noor" to MasjidTranslation(
        nameUr = "مسجد نور",
        nameHi = "मस्जिद-ए-نور",
        areaUr = "بنجارہ ہلز",
        areaHi = "बंजारा हिल्स",
        cityUr = "حیدرآباد",
        cityHi = "हैदराबाद"
    ),
    "hyd_darussalam" to MasjidTranslation(
        nameUr = "دارالسلام مسجد",
        nameHi = "दारुस्सलाम मस्जिद",
        areaUr = "آغا پورہ",
        areaHi = "आगापुरा",
        cityUr = "حیدرآباد",
        cityHi = "हैदराबाद"
    ),
    "hyd_chaderghat" to MasjidTranslation(
        nameUr = "مسجد بلال",
        nameHi = "मस्जिद-ए-बिलाल",
        areaUr = "چادر گھاٹ",
        areaHi = "चादरघाट",
        cityUr = "حیدرآباد",
        cityHi = "हैदराबाद"
    ),
    "tg_warangal_jama" to MasjidTranslation(
        nameUr = "شاہی جامع مسجد",
        nameHi = "शाही जामा मस्जिद",
        areaUr = "قاضی پیٹ",
        areaHi = "काज़ीपेट",
        cityUr = "وارنگل",
        cityHi = "वारंगल"
    ),
    "tg_nizamabad_jama" to MasjidTranslation(
        nameUr = "بڑی مسجد",
        nameHi = "बड़ी मस्जिद",
        areaUr = "برکت پورہ",
        areaHi = "बरकतपुरा",
        cityUr = "نظام آباد",
        cityHi = "निज़ामाबाद"
    ),

    // Kolkata & West Bengal
    "kol_nakhoda" to MasjidTranslation(
        nameUr = "ناخدا مسجد",
        nameHi = "नाख़ुदा मस्जिद",
        areaUr = "چتپور",
        areaHi = "चितपुर",
        cityUr = "کولکتہ",
        cityHi = "कोलकाता"
    ),
    "kol_tipu_sultan" to MasjidTranslation(
        nameUr = "ٹیپو سلطان شاہی مسجد",
        nameHi = "टीपू सुल्तान शाही मस्जिद",
        areaUr = "دھرم تلا / ایسپلینیڈ",
        areaHi = "धर्मतला / एस्प्लेनेड",
        cityUr = "کولکتہ",
        cityHi = "कोलकाता"
    ),
    "kol_basri" to MasjidTranslation(
        nameUr = "بصری شاہ مسجد",
        nameHi = "बसरी शाह मस्जिद",
        areaUr = "سیٹھ پوکور روڈ",
        areaHi = "सेठ पुकुर रोड",
        cityUr = "کولکتہ",
        cityHi = "कोलकाता"
    ),
    "kol_rajabazar" to MasjidTranslation(
        nameUr = "راجہ بازار جامع مسجد",
        nameHi = "राजाबाज़ार जामा मस्जिद",
        areaUr = "نارکل ڈانگا مین روڈ",
        areaHi = "नरकेलडांगा मेन रोड",
        cityUr = "کولکتہ",
        cityHi = "कोलकाता"
    ),
    "kol_park_circus" to MasjidTranslation(
        nameUr = "پارک سرکس شاہی مسجد",
        nameHi = "पार्क सर्कस शाही मस्जिद",
        areaUr = "پارک سرکس",
        areaHi = "पार्क सर्कस",
        cityUr = "کولکتہ",
        cityHi = "कोलकाता"
    ),
    "kol_metiabruz" to MasjidTranslation(
        nameUr = "مٹیا برج شاہی جامع مسجد",
        nameHi = "मटियाबुर्ज शाही जामा मस्जिद",
        areaUr = "گارڈن ریچ",
        areaHi = "गार्डन रीच",
        cityUr = "کولکتہ",
        cityHi = "कोलकाता"
    ),
    "kol_khidirpur" to MasjidTranslation(
        nameUr = "خضر پور شاہی جامع مسجد",
        nameHi = "खिदिरपुर शाही जामा मस्जिद",
        areaUr = "خضر پور",
        areaHi = "खिदिरपुर",
        cityUr = "کولکتہ",
        cityHi = "कोलकाता"
    ),
    "kol_colootola" to MasjidTranslation(
        nameUr = "کولوٹولا بڑی مسجد",
        nameHi = "कोलूतला बड़ी मस्जिद",
        areaUr = "فئیرز لین",
        areaHi = "फियर्स लेन",
        cityUr = "کولکتہ",
        cityHi = "कोलकाता"
    ),
    "kol_hooghly" to MasjidTranslation(
        nameUr = "ہگلی امام باڑہ مسجد",
        nameHi = "हुगली इमामबाड़ा मस्जिद",
        areaUr = "ہگلی گھاٹ",
        areaHi = "हुगली घाट",
        cityUr = "ہگلی",
        cityHi = "हुगली"
    ),
    "wb_asansol_jama" to MasjidTranslation(
        nameUr = "آسنسول مرکزی جامع مسجد",
        nameHi = "आसनसोल सेंट्रल जामा मस्जिद",
        areaUr = "ریل پار",
        areaHi = "रेलपार",
        cityUr = "آسنسول",
        cityHi = "आसनसोल"
    ),
    "wb_siliguri_jama" to MasjidTranslation(
        nameUr = "سلی گوڑی جامع مسجد",
        nameHi = "सिलीगुड़ी जामा मस्जिद",
        areaUr = "ہسپتال روڈ",
        areaHi = "अस्पताल रोड",
        cityUr = "سلی گوڑی",
        cityHi = "सिलीगुड़ी"
    ),

    // Lucknow & Uttar Pradesh
    "lko_asfi" to MasjidTranslation(
        nameUr = "آصفی مسجد (بڑا امام باڑہ)",
        nameHi = "आसफ़ी मस्जिद (बड़ा इमामबाड़ा)",
        areaUr = "حسین آباد",
        areaHi = "हुसैनाबाद",
        cityUr = "لکھنؤ",
        cityHi = "लखनऊ"
    ),
    "lko_tile_wali" to MasjidTranslation(
        nameUr = "ٹیلے والی مسجد",
        nameHi = "टीले वाली मस्जिद",
        areaUr = "گومتی کنارہ، چوک",
        areaHi = "गोमती तट, चौक",
        cityUr = "لکھنؤ",
        cityHi = "लखनऊ"
    ),
    "lko_tehsin" to MasjidTranslation(
        nameUr = "جامع مسجد تحسین گنج",
        nameHi = "जामा मस्जिद तहसीन गंज",
        areaUr = "حسین آباد",
        areaHi = "हुसैनाबाद",
        cityUr = "لکھنؤ",
        cityHi = "लखनऊ"
    ),
    "lko_nadwa" to MasjidTranslation(
        nameUr = "ندوۃ العلماء مرکزی مسجد",
        nameHi = "नदवतुल उलमा केंद्रीय मस्जिद",
        areaUr = "ٹیگور مارگ",
        areaHi = "टैगोर मार्ग",
        cityUr = "لکھنؤ",
        cityHi = "लखनऊ"
    ),
    "lko_aishbagh" to MasjidTranslation(
        nameUr = "عیش باغ عیدگاہ مسجد",
        nameHi = "ऐशबाग़ ईदगाह मस्जिद",
        areaUr = "عیش باغ",
        areaHi = "ऐशबाग़",
        cityUr = "لکھنؤ",
        cityHi = "लखनऊ"
    ),
    "lko_aminabad" to MasjidTranslation(
        nameUr = "امینہ آباد جامع مسجد",
        nameHi = "अमीनाबाद जामा मस्जिद",
        areaUr = "امینہ آباد",
        areaHi = "अमीनाबाद",
        cityUr = "لکھنؤ",
        cityHi = "लखनऊ"
    ),
    "knp_moolganj" to MasjidTranslation(
        nameUr = "جامع مسجد مول گنج",
        nameHi = "जामा मस्जिद मूलगंज",
        areaUr = "مول گنج مارکیٹ",
        areaHi = "मूलगंज बाज़ार",
        cityUr = "کانپور",
        cityHi = "कानपुर"
    ),
    "knp_parade" to MasjidTranslation(
        nameUr = "پریڈ بڑی مسجد",
        nameHi = "परेड बड़ी मस्जिद",
        areaUr = "پریڈ گراؤنڈ",
        areaHi = "परेड ग्राउंड",
        cityUr = "کانپور",
        cityHi = "कानपुर"
    ),
    "knp_halim" to MasjidTranslation(
        nameUr = "حلیم مسلم کیمپس مسجد",
        nameHi = "हलीम मुस्लिम कैंपस मस्जिद",
        areaUr = "چمن گنج",
        areaHi = "चमनगंज",
        cityUr = "کانپور",
        cityHi = "कानपुर"
    ),
    "ali_amu_sir_syed" to MasjidTranslation(
        nameUr = "سر سید ہال مسجد",
        nameHi = "सर सैयद हॉल मस्जिद",
        areaUr = "اے ایم یو کیمپس",
        areaHi = "एएमयू कैंपस",
        cityUr = "علی گڑھ",
        cityHi = "अलीगढ़"
    ),
    "ali_univ_jama" to MasjidTranslation(
        nameUr = "یونیورسٹی جامع مسجد",
        nameHi = "यूनिवर्सिटी जामा मस्जिद",
        areaUr = "اے ایم یو",
        areaHi = "एएमयू",
        cityUr = "علی گڑھ",
        cityHi = "अलीगढ़"
    ),
    "bly_aala_hazrat" to MasjidTranslation(
        nameUr = "درگاہ اعلیٰ حضرت مسجد",
        nameHi = "दरगाह-ए-आला हज़रत मस्जिद",
        areaUr = "سوداگران",
        areaHi = "सौदागरान",
        cityUr = "بریلی",
        cityHi = "बरेली"
    ),
    "bly_shahi" to MasjidTranslation(
        nameUr = "شاہی جامع مسجد",
        nameHi = "शाही जामा मस्जिद",
        areaUr = "بڑا بازار",
        areaHi = "बड़ा बाज़ार",
        cityUr = "بریلی",
        cityHi = "बरेली"
    ),
    "vns_gyanvapi" to MasjidTranslation(
        nameUr = "گیان واپی / عالمگیر مسجد",
        nameHi = "ज्ञानवापी / आलमगीर मस्जिद",
        areaUr = "چوک",
        areaHi = "चौक",
        cityUr = "وارانسی",
        cityHi = "वाराणसी"
    ),
    "agr_jama" to MasjidTranslation(
        nameUr = "جامع مسجد کناری بازار",
        nameHi = "जामा मस्जिद किनारी बाज़ार",
        areaUr = "کناری بازار",
        areaHi = "किनारी बाज़ार",
        cityUr = "آگرہ",
        cityHi = "आगरा"
    ),
    "agr_fatehpur" to MasjidTranslation(
        nameUr = "فتح پور سیکری جامع مسجد",
        nameHi = "फ़तेहपुर सीकरी जामा मस्जिद",
        areaUr = "فتح پور سیکری",
        areaHi = "फ़तेहपुर सीकरी",
        cityUr = "آگرہ",
        cityHi = "आगरा"
    ),
    "mrt_kotwali" to MasjidTranslation(
        nameUr = "جامع مسجد کوتوالی",
        nameHi = "जामा मस्जिद कोतवाली",
        areaUr = "کوتوالی",
        areaHi = "कोतवाली",
        cityUr = "میرٹھ",
        cityHi = "मेरठ"
    ),
    "sre_saharanpur" to MasjidTranslation(
        nameUr = "بڑی جامع مسجد",
        nameHi = "बड़ी जामा मस्जिद",
        areaUr = "چوک",
        areaHi = "चौक",
        cityUr = "سہارنپور",
        cityHi = "सहारनपुर"
    ),
    "gkp_gorakhpur" to MasjidTranslation(
        nameUr = "جامع مسجد اردو بازار",
        nameHi = "जामा मस्जिद उर्दू बाज़ार",
        areaUr = "اردو بازار",
        areaHi = "उर्दू बाज़ार",
        cityUr = "گورکھپور",
        cityHi = "गोरखपुर"
    ),
    "mor_moradabad" to MasjidTranslation(
        nameUr = "شاہی جامع مسجد",
        nameHi = "शाही जामा मस्जिद",
        areaUr = "کورٹ روڈ",
        areaHi = "कोर्ट रोड",
        cityUr = "مرادآباد",
        cityHi = "मुरादाबाद"
    ),

    // Bangalore & Karnataka
    "blr_city_market" to MasjidTranslation(
        nameUr = "سٹی جامع مسجد",
        nameHi = "सिटी जामा मस्जिद",
        areaUr = "سٹی مارکیٹ",
        areaHi = "सिटी मार्केट",
        cityUr = "بنگلور",
        cityHi = "बैंगलोर"
    ),
    "blr_shivajinagar" to MasjidTranslation(
        nameUr = "جامعہ مسجد شیواجی نگر",
        nameHi = "जामिया मस्जिद शिवाजीनगर",
        areaUr = "شیواجی نگر",
        areaHi = "शिवाजीनगर",
        cityUr = "بنگلور",
        cityHi = "बैंगलोर"
    ),
    "blr_bilal" to MasjidTranslation(
        nameUr = "مسجد بلال",
        nameHi = "मस्जिद-ए-बिलाल",
        areaUr = "بنیرگھٹہ روڈ",
        areaHi = "बन्नेरघट्टा रोड",
        cityUr = "بنگلور",
        cityHi = "बैंगलोर"
    ),
    "blr_tawakkal" to MasjidTranslation(
        nameUr = "توکل مستان مسجد",
        nameHi = "तवक्कल मस्तान मस्जिद",
        areaUr = "کٹن پیٹ",
        areaHi = "कॉटनपेट",
        cityUr = "بنگلور",
        cityHi = "बैंगलोर"
    ),
    "blr_commercial" to MasjidTranslation(
        nameUr = "مدینہ مسجد",
        nameHi = "मदीना मस्जिद",
        areaUr = "کمرشل اسٹریٹ",
        areaHi = "कमर्शियल स्ट्रीट",
        cityUr = "بنگلور",
        cityHi = "बैंगलोर"
    ),
    "blr_khadria" to MasjidTranslation(
        nameUr = "مسجد خادریہ",
        nameHi = "मस्जिद-ए-ख़ादरिया",
        areaUr = "ملرز روڈ",
        areaHi = "मिलर्स रोड",
        cityUr = "بنگلور",
        cityHi = "बैंगलोर"
    ),
    "blr_frazer" to MasjidTranslation(
        nameUr = "فریزر ٹاؤن مرکزی مسجد",
        nameHi = "फ्रेज़र टाउन सेंट्रल मस्जिद",
        areaUr = "کولز روڈ",
        areaHi = "कोल्स रोड",
        cityUr = "بنگلور",
        cityHi = "बैंगलोर"
    ),
    "blr_jayanagar" to MasjidTranslation(
        nameUr = "جئے نگر جامع مسجد",
        nameHi = "जयनगर 4th ब्लॉक जामा मस्जिद",
        areaUr = "جئے نگر",
        areaHi = "जयनगर",
        cityUr = "بنگلور",
        cityHi = "बैंगलोर"
    ),
    "mys_jamia" to MasjidTranslation(
        nameUr = "جامعہ مسجد اشوکا روڈ",
        nameHi = "जामिया मस्जिद अशोका रोड",
        areaUr = "لشکر محلہ",
        areaHi = "लश्कर मोहल्ला",
        cityUr = "میسور",
        cityHi = "मैसूर"
    ),
    "glb_shahi" to MasjidTranslation(
        nameUr = "شاہی مسجد گلبرگہ",
        nameHi = "शाही मस्जिद गुलबर्गा",
        areaUr = "قلعہ علاقہ",
        areaHi = "क़िला क्षेत्र",
        cityUr = "گلبرگہ",
        cityHi = "कलबुर्गी (गुलबर्गा)"
    ),
    "hbl_hubli" to MasjidTranslation(
        nameUr = "بڑی جامع مسجد ہبلی",
        nameHi = "बड़ी जामा मस्जिद हुबली",
        areaUr = "داجی پیٹھ",
        areaHi = "दाजी पेठ",
        cityUr = "ہبلی",
        cityHi = "हुबली"
    ),
    "mng_mangalore" to MasjidTranslation(
        nameUr = "زینت بخش جمعہ مسجد",
        nameHi = "ज़ीनत बख़्श जुमा मस्जिद",
        areaUr = "بندر",
        areaHi = "बंदर",
        cityUr = "منگلور",
        cityHi = "मंगलौर"
    ),
    "bel_belgaum" to MasjidTranslation(
        nameUr = "جامع مسجد بیلگام",
        nameHi = "जामा मस्जिद बेलगाम",
        areaUr = "کیمپ",
        areaHi = "कैंप",
        cityUr = "بیلگام",
        cityHi = "बेलगाम"
    ),

    // Chennai & Tamil Nadu
    "chn_wallajah" to MasjidTranslation(
        nameUr = "والاجاہ بڑی مسجد",
        nameHi = "वालाजाह बड़ी मस्जिद",
        areaUr = "ٹرپلیکین",
        areaHi = "ट्रिपलिकेन",
        cityUr = "چنئی",
        cityHi = "चेन्नई"
    ),
    "chn_thousand_lights" to MasjidTranslation(
        nameUr = "ہزار لائٹس مسجد",
        nameHi = "थाउज़ेंड लाइट्स मस्जिद",
        areaUr = "انا سالائی",
        areaHi = "अन्ना सलाई",
        cityUr = "چنئی",
        cityHi = "चेन्नई"
    ),
    "chn_makkah" to MasjidTranslation(
        nameUr = "مکہ مسجد ماؤنٹ روڈ",
        nameHi = "मक्का मस्जिद माउंट रोड",
        areaUr = "انا سالائی",
        areaHi = "अन्ना सलाई",
        cityUr = "چنئی",
        cityHi = "चेन्नई"
    ),
    "chn_madhavaram" to MasjidTranslation(
        nameUr = "مادھاورم جامع مسجد",
        nameHi = "माधवरम जामा मस्जिद",
        areaUr = "مادھاورم",
        areaHi = "माधवरम",
        cityUr = "چنئی",
        cityHi = "चेन्नई"
    ),
    "chn_royapettah" to MasjidTranslation(
        nameUr = "رائل پیٹہ جامع مسجد",
        nameHi = "रोयापेट्टाह जामा मस्जिद",
        areaUr = "رائل پیٹہ",
        areaHi = "रोयापेट्टाह",
        cityUr = "چنئی",
        cityHi = "चेन्नई"
    ),
    "chn_perambur" to MasjidTranslation(
        nameUr = "پیرمبور شاہی مسجد",
        nameHi = "पेराम्बूर शाही मस्जिद",
        areaUr = "پیرمبور",
        areaHi = "पेराम्बूर",
        cityUr = "چنئی",
        cityHi = "चेन्नई"
    ),
    "tn_nagore" to MasjidTranslation(
        nameUr = "ناگور درگاہ شاہی مسجد",
        nameHi = "नागोर दरगाह शाही मस्जिद",
        areaUr = "ناگور",
        areaHi = "नागोर",
        cityUr = "ناگاپٹنم",
        cityHi = "नागापट्टिनम"
    ),
    "tn_madurai" to MasjidTranslation(
        nameUr = "قاضی مار بڑی مسجد",
        nameHi = "क़ाज़ीमार बड़ी मस्जिद",
        areaUr = "قاضی مار اسٹریٹ",
        areaHi = "क़ाज़ीमार स्ट्रीट",
        cityUr = "مدورائی",
        cityHi = "मदुरै"
    ),
    "tn_trichy" to MasjidTranslation(
        nameUr = "ترچی بڑی مسجد",
        nameHi = "त्रिची बड़ी मस्जिद",
        areaUr = "پلک کرائی",
        areaHi = "पलाक्कराई",
        cityUr = "تروچراپلی",
        cityHi = "तिरुचिरापल्ली"
    ),
    "tn_coimbatore" to MasjidTranslation(
        nameUr = "عطر جماعت مسجد",
        nameHi = "अथार जमात मस्जिद",
        areaUr = "بڑا بازار اسٹریٹ",
        areaHi = "बिग बाज़ार स्ट्रीट",
        cityUr = "کوئمبٹور",
        cityHi = "कोयंबटूर"
    ),

    // Ahmedabad & Gujarat
    "amd_jama" to MasjidTranslation(
        nameUr = "جامع مسجد مانک چوک",
        nameHi = "जामा मस्जिद मानक चौक",
        areaUr = "مانک چوک",
        areaHi = "मानक चौक",
        cityUr = "احمد آباد",
        cityHi = "अहमदाबाद"
    ),
    "amd_sidi_saiyyed" to MasjidTranslation(
        nameUr = "سیدی سید مسجد (جالی والی)",
        nameHi = "सीदी सैय्यद मस्जिद",
        areaUr = "لال دروازہ",
        areaHi = "लाल दरवाज़ा",
        cityUr = "احمد آباد",
        cityHi = "अहमदाबाद"
    ),
    "amd_sarkhej" to MasjidTranslation(
        nameUr = "سرکھیج روزہ مسجد",
        nameHi = "सरखेज रोज़ा मस्जिद",
        areaUr = "مکریا",
        areaHi = "मकरबा",
        cityUr = "احمد آباد",
        cityHi = "अहमदाबाद"
    ),
    "amd_rani_sipri" to MasjidTranslation(
        nameUr = "رانی سپری مسجد",
        nameHi = "रानी सिपरी मस्जिद",
        areaUr = "استودیہ",
        areaHi = "अस्टोदिया",
        cityUr = "احمد آباد",
        cityHi = "अहमदाबाद"
    ),
    "amd_shah_alam" to MasjidTranslation(
        nameUr = "شاہ عالم روزہ مسجد",
        nameHi = "शाह-ए-आलम रोज़ा मस्जिद",
        areaUr = "شاہ عالم",
        areaHi = "शाह आलम",
        cityUr = "احمد آباد",
        cityHi = "अहमदाबाद"
    ),
    "amd_juhapura" to MasjidTranslation(
        nameUr = "جوہاپورہ مرکز مسجد",
        nameHi = "जुहापुरा मरकज़ मस्जिद",
        areaUr = "سرکھیج روڈ",
        areaHi = "सरखेज रोड",
        cityUr = "احمد آباد",
        cityHi = "अहमदाबाद"
    ),
    "sur_rander" to MasjidTranslation(
        nameUr = "مغل مسجد راندیر",
        nameHi = "मुग़ल मस्जिद रांदेर",
        areaUr = "راندیر",
        areaHi = "रांदेर",
        cityUr = "سورت",
        cityHi = "सूरत"
    ),
    "sur_nanpura" to MasjidTranslation(
        nameUr = "جامع مسجد نان پورہ",
        nameHi = "जामा मस्जिद नानपुरा",
        areaUr = "نان پورہ",
        areaHi = "नानपुरा",
        cityUr = "سورت",
        cityHi = "सूरत"
    ),
    "guj_navsari" to MasjidTranslation(
        nameUr = "نوساری بڑی مسجد",
        nameHi = "नवसारी बड़ी मस्जिद",
        areaUr = "موٹا بازار",
        areaHi = "मोटा बाज़ार",
        cityUr = "نوساری",
        cityHi = "नवसारी"
    ),
    "vad_mandvi" to MasjidTranslation(
        nameUr = "جامع مسجد مانڈوی",
        nameHi = "जामा मस्जिद मांडवी",
        areaUr = "مانڈوی گیٹ",
        areaHi = "मांडवी गेट",
        cityUr = "وڈودرا",
        cityHi = "वडोदरा"
    ),
    "guj_rajkot" to MasjidTranslation(
        nameUr = "راجکوٹ جامع مسجد",
        nameHi = "राजकोट जामा मस्जिद",
        areaUr = "دھرمیندر روڈ",
        areaHi = "धर्मेंद्र रोड",
        cityUr = "راجکوٹ",
        cityHi = "राजकोट"
    ),
    "guj_bhavnagar" to MasjidTranslation(
        nameUr = "بھاونگر جامع مسجد",
        nameHi = "भावनगर जामा मस्जिद",
        areaUr = "کھار گیٹ",
        areaHi = "खार गेट",
        cityUr = "بھاونگر",
        cityHi = "भावनगर"
    ),

    // Bhopal & Madhya Pradesh
    "bho_taj_ul" to MasjidTranslation(
        nameUr = "تاج المساجد",
        nameHi = "ताज-उल-मसाजिद",
        areaUr = "کوہ فضا",
        areaHi = "कोहेफिज़ा",
        cityUr = "بھوپال",
        cityHi = "भोपाल"
    ),
    "bho_moti" to MasjidTranslation(
        nameUr = "موتی مسجد",
        nameHi = "मोती मस्जिद",
        areaUr = "ہوا محل روڈ",
        areaHi = "हवामहल रोड",
        cityUr = "بھوپال",
        cityHi = "भोपाल"
    ),
    "bho_dhai" to MasjidTranslation(
        nameUr = "ڈھائی سیڑھی کی مسجد",
        nameHi = "ढाई सीढ़ी की मस्जिद",
        areaUr = "گاندھی میڈیکل کیمپس",
        areaHi = "गांधी मेडिकल कैंपस",
        cityUr = "بھوپال",
        cityHi = "भोपाल"
    ),
    "bho_chowk" to MasjidTranslation(
        nameUr = "جامع مسجد چوک",
        nameHi = "जामा मस्जिद चौक",
        areaUr = "ابراہیم پورہ",
        areaHi = "इब्राहिमपुरा",
        cityUr = "بھوپال",
        cityHi = "भोपाल"
    ),
    "bho_tarjuma" to MasjidTranslation(
        nameUr = "مسجد ترجمہ",
        nameHi = "मस्जिद तर्जुमा",
        areaUr = "شاہجہاں آباد",
        areaHi = "शाहजहाँनाबाद",
        cityUr = "بھوپال",
        cityHi = "भोपाल"
    ),
    "bho_idgah" to MasjidTranslation(
        nameUr = "عیدگاہ ہلز شاہی مسجد",
        nameHi = "ईदगाह हिल्स शाही मस्जिद",
        areaUr = "عیدگاہ ہلز",
        areaHi = "ईदगाह हिल्स",
        cityUr = "بھوپال",
        cityHi = "भोपाल"
    ),
    "mp_indore" to MasjidTranslation(
        nameUr = "جامع مسجد اندور",
        nameHi = "जामा मस्जिद इंदौर",
        areaUr = "بڑا صرافہ",
        areaHi = "बड़ा सराफ़ा",
        cityUr = "اندور",
        cityHi = "इंदौर"
    ),
    "mp_gwalior" to MasjidTranslation(
        nameUr = "شاہی مسجد گوالیار قلعہ",
        nameHi = "शाही मस्जिद ग्वालियर क़िला",
        areaUr = "گوالیار قلعہ",
        areaHi = "ग्वालियर क़िला",
        cityUr = "گوالیار",
        cityHi = "ग्वालियर"
    ),
    "mp_jabalpur" to MasjidTranslation(
        nameUr = "بڑی جامع مسجد جبل پور",
        nameHi = "बड़ी जामा मस्जिद जबलपुर",
        areaUr = "لارڈ گنج",
        areaHi = "लार्डगंज",
        cityUr = "جبل پور",
        cityHi = "जबलपुर"
    ),
    "mp_ujjain" to MasjidTranslation(
        nameUr = "جامع مسجد اجین",
        nameHi = "जामा मस्जिद उज्जैन",
        areaUr = "بیگم باغ",
        areaHi = "बेगम बाग़",
        cityUr = "اجین",
        cityHi = "उज्जैन"
    ),

    // Srinagar & Jammu & Kashmir
    "srn_jamia" to MasjidTranslation(
        nameUr = "جامع مسجد سری نگر",
        nameHi = "जामिया मस्जिद श्रीनगर",
        areaUr = "نوہٹہ",
        areaHi = "नौहट्टा",
        cityUr = "سری نگر",
        cityHi = "श्रीनगर"
    ),
    "srn_hazratbal" to MasjidTranslation(
        nameUr = "درگاہ حضرت بل مسجد",
        nameHi = "हज़रतबल दरगाह मस्जिद",
        areaUr = "ڈل جھیل",
        areaHi = "डल झील",
        cityUr = "سری نگر",
        cityHi = "श्रीनगर"
    ),
    "srn_khanqah" to MasjidTranslation(
        nameUr = "خانقاہ معلیٰ شاہ ہمدان",
        nameHi = "शाह-ए-हमदान ख़ानक़ाह",
        areaUr = "زینہ کدل",
        areaHi = "ज़ैना कदल",
        cityUr = "سری نگر",
        cityHi = "श्रीनगर"
    ),
    "srn_dastgeer" to MasjidTranslation(
        nameUr = "دستگیر صاحب مسجد",
        nameHi = "दस्तगीर साहिब मस्जिद",
        areaUr = "خانیار",
        areaHi = "खानयार",
        cityUr = "سری نگر",
        cityHi = "श्रीनगर"
    ),
    "srn_pathar" to MasjidTranslation(
        nameUr = "پتھر مسجد",
        nameHi = "पत्थर मस्जिद",
        areaUr = "ہیریٹیج زون",
        areaHi = "हेरिटेज ज़ोन",
        cityUr = "سری نگر",
        cityHi = "श्रीनगर"
    ),
    "srn_aali" to MasjidTranslation(
        nameUr = "عالی مسجد",
        nameHi = "आली मस्जिद",
        areaUr = "عیدگاہ گراؤنڈ",
        areaHi = "ईदगाह मैदान",
        cityUr = "سری نگر",
        cityHi = "श्रीनगर"
    ),
    "jk_jammu_tawi" to MasjidTranslation(
        nameUr = "جامع مسجد تالاب کھٹیکاں",
        nameHi = "जामिया मस्जिद तालाब खटीकां",
        areaUr = "تالاب کھٹیکاں",
        areaHi = "तालाब खटीकां",
        cityUr = "جموں",
        cityHi = "जम्मू"
    ),
    "jk_anantnag" to MasjidTranslation(
        nameUr = "ریشی صاحب جامع مسجد",
        nameHi = "ऋषि साहिब जामा मस्जिद",
        areaUr = "کے پی روڈ",
        areaHi = "के.पी. रोड",
        cityUr = "اننت ناگ",
        cityHi = "अनंतनाग"
    ),

    // Bihar & Jharkhand
    "pat_pathar" to MasjidTranslation(
        nameUr = "پتھر کی مسجد",
        nameHi = "पत्थर की मस्जिद",
        areaUr = "گنگا پتھ",
        areaHi = "गंगा पथ",
        cityUr = "پٹنہ",
        cityHi = "पटना"
    ),
    "pat_digha" to MasjidTranslation(
        nameUr = "دیگھا جامع مسجد",
        nameHi = "दीघा जामा मस्जिद",
        areaUr = "دیگھا گھاٹ",
        areaHi = "दीघा घाट",
        cityUr = "پٹنہ",
        cityHi = "पटना"
    ),
    "pat_sabzibagh" to MasjidTranslation(
        nameUr = "سبزی باغ بڑی مسجد",
        nameHi = "सब्ज़ीबाग़ बड़ी मस्जिद",
        areaUr = "سبزی باغ",
        areaHi = "सब्ज़ीबाग़",
        cityUr = "پٹنہ",
        cityHi = "पटना"
    ),
    "pat_phulwari" to MasjidTranslation(
        nameUr = "خانقاہ مجیبیہ شاہی مسجد",
        nameHi = "ख़ानक़ाह मुजीबिया शाही मस्जिद",
        areaUr = "پھلواری شریف",
        areaHi = "फुलवारी शरीफ़",
        cityUr = "پٹنہ",
        cityHi = "पटना"
    ),
    "bih_maner" to MasjidTranslation(
        nameUr = "شاہی مسجد منیر شریف",
        nameHi = "शाही मस्जिद मनेर शरीफ़",
        areaUr = "منیر",
        areaHi = "मनेर",
        cityUr = "پٹنہ دیہی",
        cityHi = "पटना ग्रामीण"
    ),
    "bih_gaya" to MasjidTranslation(
        nameUr = "گیا شاہی جامع مسجد",
        nameHi = "गया शाही जामा मस्जिद",
        areaUr = "چوک",
        areaHi = "चौक",
        cityUr = "گیا",
        cityHi = "गया"
    ),
    "bih_bhagalpur" to MasjidTranslation(
        nameUr = "شاہی جامع مسجد چمپا نگر",
        nameHi = "शाही जामा मस्जिद चंपानगर",
        areaUr = "چمپا نگر",
        areaHi = "चंपानगर",
        cityUr = "بھاگلپور",
        cityHi = "भागलपुर"
    ),
    "bih_muzaffarpur" to MasjidTranslation(
        nameUr = "بڑی مسجد کمپنی باغ",
        nameHi = "बड़ी मस्जिद कंपनी बाग़",
        areaUr = "کمپنی باغ",
        areaHi = "कंपनी बाग़",
        cityUr = "مظفرپور",
        cityHi = "मुज़फ़्फ़रपुर"
    ),
    "jhk_ranchi" to MasjidTranslation(
        nameUr = "مین روڈ بڑی مسجد",
        nameHi = "मेन रोड बड़ी मस्जिद",
        areaUr = "ہند پیڑھی، مین روڈ",
        areaHi = "हिंदपीढ़ी, मेन रोड",
        cityUr = "رانچی",
        cityHi = "राँची"
    ),
    "jhk_jamshedpur" to MasjidTranslation(
        nameUr = "ساکچی مرکزی جامع مسجد",
        nameHi = "साकची सेंट्रल जामा मस्जिद",
        areaUr = "ساکچی مارکیٹ",
        areaHi = "साकची मार्केट",
        cityUr = "جمشید پور",
        cityHi = "जमशेदपुर"
    ),
    "jhk_dhanbad" to MasjidTranslation(
        nameUr = "بڑی مسجد پرانا بازار",
        nameHi = "बड़ी मस्जिद पुराना बाज़ार",
        areaUr = "پرانا بازار",
        areaHi = "पुराना बाज़ार",
        cityUr = "دھنباد",
        cityHi = "धनबाद"
    ),

    // Rajasthan
    "ajm_akbari" to MasjidTranslation(
        nameUr = "درگاہ شریف اکبری مسجد",
        nameHi = "दरगाह शरीफ़ अकबरी मस्जिद",
        areaUr = "درگاہ بازار",
        areaHi = "दरगाह बाज़ार",
        cityUr = "اجمیر",
        cityHi = "अजमेर"
    ),
    "ajm_shah_jahani" to MasjidTranslation(
        nameUr = "شاہ جہانی مسجد",
        nameHi = "शाहजहानी मस्जिद",
        areaUr = "درگاہ شریف",
        areaHi = "दरगाह शरीफ़",
        cityUr = "اجمیر",
        cityHi = "अजमेर"
    ),
    "ajm_adhai_din" to MasjidTranslation(
        nameUr = "اڑھائی دن کا جھونپڑا مسجد",
        nameHi = "अढ़ाई दिन का झोंपड़ा मस्जिद",
        areaUr = "درگاہ دامن",
        areaHi = "दरगाह तलहटी",
        cityUr = "اجمیر",
        cityHi = "अजमेर"
    ),
    "jai_johari" to MasjidTranslation(
        nameUr = "جامع مسجد جوہری بازار",
        nameHi = "जामा मस्जिद जौहरी बाज़ार",
        areaUr = "جوہری بازار",
        areaHi = "जौहरी बाज़ार",
        cityUr = "جے پور",
        cityHi = "जयपुर"
    ),
    "jai_char_darwaza" to MasjidTranslation(
        nameUr = "چار دروازہ جامع مسجد",
        nameHi = "चार दरवाज़ा जामा मस्जिद",
        areaUr = "رام گنج",
        areaHi = "रामगंज",
        cityUr = "جے پور",
        cityHi = "जयपुर"
    ),
    "jai_moti_doongri" to MasjidTranslation(
        nameUr = "مسلم مسافر خانہ مسجد",
        nameHi = "मुस्लिम मुसाफ़िरख़ाना मस्जिद",
        areaUr = "موتی ڈونگری روڈ",
        areaHi = "मोती डूंगरी रोड",
        cityUr = "جے پور",
        cityHi = "जयपुर"
    ),
    "raj_jodhpur" to MasjidTranslation(
        nameUr = "شاہی جامع مسجد سوجتی گیٹ",
        nameHi = "शाही जामा मस्जिद सोजती गेट",
        areaUr = "سوجتی گیٹ",
        areaHi = "सोजती गेट",
        cityUr = "جودھپور",
        cityHi = "जोधपुर"
    ),
    "raj_kota" to MasjidTranslation(
        nameUr = "بڑی جامع مسجد کوٹہ",
        nameHi = "बड़ी जामा मस्जिद कोटा",
        areaUr = "رام پورا",
        areaHi = "रामपुरा",
        cityUr = "کوٹہ",
        cityHi = "कोटा"
    ),
    "raj_udaipur" to MasjidTranslation(
        nameUr = "جامع مسجد ہاتھی پول",
        nameHi = "जामा मस्जिद हाथीपोल",
        areaUr = "ہاتھی پول",
        areaHi = "हाथीपोल",
        cityUr = "ادے پور",
        cityHi = "उदयपुर"
    ),

    // Kerala & Other Regions
    "ker_cheraman" to MasjidTranslation(
        nameUr = "چیرامن جمعہ مسجد (629ء)",
        nameHi = "चेरामन जुमा मस्जिद (629 ईस्वी)",
        areaUr = "میتھالا، کوڈنگلور",
        areaHi = "मेथाला, कोडुंगलूर",
        cityUr = "تھریسور",
        cityHi = "त्रिशूर"
    ),
    "ker_mishkal" to MasjidTranslation(
        nameUr = "مشکال مسجد کٹی چیرا",
        nameHi = "मिश्काल मस्जिद कुट्टिचिरा",
        areaUr = "کٹی چیرا",
        areaHi = "कुट्टिचिरा",
        cityUr = "کالی کٹ",
        cityHi = "कालिकट"
    ),
    "ker_muchundi" to MasjidTranslation(
        nameUr = "مچندی مسجد کٹی چیرا",
        nameHi = "मुचुंडी मस्जिद कुट्टिचिरा",
        areaUr = "کٹی چیرا",
        areaHi = "कुट्टिचिरा",
        cityUr = "کالی کٹ",
        cityHi = "कालिकट"
    ),
    "ker_palayam" to MasjidTranslation(
        nameUr = "پالائم جمعہ مسجد",
        nameHi = "पलायम जुमा मस्जिद",
        areaUr = "پالائم",
        areaHi = "पलायम",
        cityUr = "ترواننت پورم",
        cityHi = "तिरुवनंतपुरम"
    ),
    "ker_ernakulam" to MasjidTranslation(
        nameUr = "براڈوے جمعہ مسجد",
        nameHi = "ब्रॉडवे जुमा मस्जिद",
        areaUr = "میرین ڈرائیو",
        areaHi = "मरीन ड्राइव",
        cityUr = "کوچی",
        cityHi = "कोच्चि"
    ),
    "goa_panaji" to MasjidTranslation(
        nameUr = "جامع مسجد پنجی",
        nameHi = "जामा मस्जिद पणजी",
        areaUr = "فونٹیناس",
        areaHi = "फॉन्टेनहास",
        cityUr = "پنجی",
        cityHi = "पणजी"
    ),
    "asm_guwahati" to MasjidTranslation(
        nameUr = "پان بازار مرکزی جامع مسجد",
        nameHi = "पानबाज़ार सेंट्रल जामा मस्जिद",
        areaUr = "پان بازار",
        areaHi = "पानबाज़ार",
        cityUr = "گوہاٹی",
        cityHi = "गुवाहाटी"
    ),
    "asm_dibrugarh" to MasjidTranslation(
        nameUr = "ڈبروگڑھ بڑی جامع مسجد",
        nameHi = "डिब्रूगढ़ बड़ी जामा मस्जिद",
        areaUr = "مارواڑی پٹی",
        areaHi = "मारवाड़ी पट्टी",
        cityUr = "ڈبروگڑھ",
        cityHi = "डिब्रूगढ़"
    ),
    "od_cuttack" to MasjidTranslation(
        nameUr = "قدم رسول شاہی مسجد",
        nameHi = "क़दम-ए-रसूल शाही मस्जिद",
        areaUr = "درگاہ بازار",
        areaHi = "दरगाह बाज़ार",
        cityUr = "کٹک",
        cityHi = "कटक"
    ),
    "od_bhubaneswar" to MasjidTranslation(
        nameUr = "کیپٹل مسجد یونٹ 4",
        nameHi = "कैपिटल मस्जिद यूनिट 4",
        areaUr = "بھوما نگر",
        areaHi = "भौमा नगर",
        cityUr = "بھوبنیشور",
        cityHi = "भुवनेश्वर"
    ),
    "chg_raipur" to MasjidTranslation(
        nameUr = "بڑی جامع مسجد مالویہ روڈ",
        nameHi = "बड़ी जामा मस्जिद मालवीय रोड",
        areaUr = "مالویہ روڈ",
        areaHi = "मालवीय रोड",
        cityUr = "رائے پور",
        cityHi = "रायपुर"
    )
)

fun MasjidItem.getLocalizedName(lang: String): String {
    val tr = masjidTranslations[this.id]
    return when (lang) {
        "ur" -> tr?.nameUr ?: this.name
        "hi" -> tr?.nameHi ?: this.name
        else -> this.name
    }
}

fun MasjidItem.getLocalizedArea(lang: String): String {
    val tr = masjidTranslations[this.id]
    return when (lang) {
        "ur" -> tr?.areaUr ?: this.area
        "hi" -> tr?.areaHi ?: this.area
        else -> this.area
    }
}

fun MasjidItem.getLocalizedCity(lang: String): String {
    val tr = masjidTranslations[this.id]
    return when (lang) {
        "ur" -> tr?.cityUr ?: this.city
        "hi" -> tr?.cityHi ?: this.city
        else -> this.city
    }
}
