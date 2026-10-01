package com.example.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf

data class AppStrings(
    // App Header
    val solapur: String = "SOLAPUR",
    val azanTimeHeader: String = "AZAN TIME",
    val punchLine: String = "Come towards the path of Allah",
    val goToToday: String = "Go to Today",
    val english: String = "English",
    val hindi: String = "Hindi",
    val urdu: String = "Urdu",

    // Prayers
    val fajr: String = "Fajr",
    val dhuhr: String = "Zohr",
    val jumah: String = "JUM'AH",
    val asr: String = "Asr",
    val maghrib: String = "Maghrib",
    val isha: String = "Isha",
    val tahajjud: String = "Tahajjud",
    val azanLabel: String = "Azan",
    val jammatLabel: String = "Jammat",
    val next: String = "NEXT",
    val markPrayed: String = "Mark as Prayed",
    val toggleAudio: String = "Toggle Audio",

    // Rak'ats Breakdown
    val prayerType: String = "PRAYER TYPE",
    val rakats: String = "RAK'ATS",
    val sunnat: String = "Sunnat",
    val farz: String = "Farz",
    val nafil: String = "Nafil",
    val witr: String = "Witr",
    val total: String = "Total",

    // Eid Cards
    val eidUlFitr: String = "Eid-ul-Fitr",
    val eidUlAdha: String = "Eid-ul-Adha",
    val eidFitrDate: String = "09 Mar 2027",
    val eidFitrDay: String = "Tuesday",
    val eidAdhaDate: String = "16 May 2027",
    val eidAdhaDay: String = "Sunday",

    // Tracker Board Summary & Stats
    val boardTitle: String = "365 DAY'S TAQWA",
    val points: String = "Taqwa Points",
    val ptsUnit: String = "pts",
    val ptsPerPrayerSubtitle: String = "1 pt per prayer",
    val perfectDays: String = "Perfect Days",
    val fiveOfFiveSubtitle: String = "6/6 Namaz prayed",
    val streak: String = "Streak",
    val daysUnit: String = "days",
    val maxStreakSubtitle: String = "Max %d days",
    val todaysPrayers: String = "Today's Prayers",
    val prayerStats: String = "Today's Prayer Stats",
    val namazTracker: String = "NAMAZ",
    val prayedTitle: String = "PRAYED",
    val missedTitle: String = "MISSED",
    val close: String = "Close",

    // Day Details Dialog
    val todayLabel: String = "TODAY",
    val yesterdayLabel: String = "YESTERDAY",
    val activeDateHint: String = "Active Date: Tap prayer to toggle (+1 pt each)",
    val historicalRecordHint: String = "Historical Record (Read-Only)",
    val scheduledAt: String = "Scheduled at %s",
    val yetToPerform: String = "Yet to perform",
    val plusOnePoint: String = "+1 Point",
    val tapToMark: String = "Tap to mark",
    val zeroPt: String = "0 pt",
    val totalPointsForDay: String = "Total Points for Day",
    val pointsOutOfFive: String = "%d / 6 Points",

    // 1-Month History Calendar Accordion
    val oneMonthHistory: String = "1-MONTH POINT HISTORY",
    val tapToExpand: String = "Tap to expand",
    val tapToCollapse: String = "Tap to collapse",
    val calendarRuleSubtitle: String = "1 pt per Namaz • Max 6 pts/day",
    val legendZero: String = "0 (Red)",
    val legendOneToFour: String = "1-5",
    val legendFive: String = "6 (Gold)",
    val legendToday: String = "Today",

    // Edit Prayer Points Section
    val editPrayerPointsTitle: String = "EDIT PRAYER POINTS (YESTERDAY & TODAY)",
    val yesterdaySimple: String = "Yesterday",
    val todaySimple: String = "Today",

    // Toast Messages
    val prayerTimeNotArrivedToast: String = "%s time is at %s. You can only mark 20 minutes after Azan time.",
    val prayerTimeNotStartedToast: String = "%s prayer time has not started yet.",

    // Zakat Calculator
    val zakatCalcBtn: String = "ZAKAT CALCULATOR",
    val zakatCalcTitle: String = "ZAKAT CALCULATOR",
    val netWealthTitle: String = "Net Wealth",
    val payableZakatTitle: String = "Payable Zakat",

    // Ramazan 2027 Section
    val ramazanTime2027: String = "RAMAZAN TIME 2027",
    val ramazanConcluded: String = "Ramazan 2027 has concluded.",
    val ramazanDateAndDay: String = "Date & Day",
    val seharTime: String = "Sehar",
    val iftarTime: String = "Iftar",
    val roja: String = "Roja",
    val totalRozaPoints: String = "Total Roza Points",
    val cannotTickBeforeDateToast: String = "Cannot tick before date arrives",
    val rozaCompletedLabel: String = "Completed",
    val rozaMissedLabel: String = "Missed",
    val todayBadge: String = "Today",

    // Ramazan Duas
    val ramazanDuasHeading: String = "Ramazan Duas",
    val duaLanguageLabel: String = "Dua Language",
    val seharDuaTitle: String = "Sehar Dua (Intention for Fasting)",
    val seharDuaArabic: String = "وَبِصَوْمِ غَدٍ نَّوَيْتُ مِنْ شَهْرِ رَمَضَانَ",
    val seharDuaTranslit: String = "Wa bisawmi ghadinn nawaytu min shahri ramadan",
    val seharDuaTranslation: String = "I intend to keep the fast tomorrow for the month of Ramadan.",

    val iftarDuaTitle: String = "Iftar Dua (Dua for Breaking Fast)",
    val iftarDuaArabic: String = "اللَّهُمَّ إِنِّي لَكَ صُمْتُ وَبِكَ آمَنْتُ وَعَلَى رِزْقِكَ أَفْطَرْتُ",
    val iftarDuaTranslit: String = "Allahumma inni laka sumtu wa bika aamantu wa 'ala rizqika aftartu",
    val iftarDuaTranslation: String = "O Allah, I fasted for You, I believe in You, and with Your provision I break my fast.",

    // Months (Full)
    val jan: String = "January",
    val feb: String = "February",
    val mar: String = "March",
    val apr: String = "April",
    val mayMonth: String = "May",
    val jun: String = "June",
    val jul: String = "July",
    val aug: String = "August",
    val sep: String = "September",
    val oct: String = "October",
    val nov: String = "November",
    val dec: String = "December",

    // Months (Short)
    val janShort: String = "Jan",
    val febShort: String = "Feb",
    val marShort: String = "Mar",
    val aprShort: String = "Apr",
    val mayShort: String = "May",
    val junShort: String = "Jun",
    val julShort: String = "Jul",
    val augShort: String = "Aug",
    val sepShort: String = "Sep",
    val octShort: String = "Oct",
    val novShort: String = "Nov",
    val decShort: String = "Dec",

    // Days of Week (Short Headers)
    val sun: String = "Sun",
    val mon: String = "Mon",
    val tue: String = "Tue",
    val wed: String = "Wed",
    val thu: String = "Thu",
    val fri: String = "Friday",
    val sat: String = "Sat",

    // Days of Week (Full)
    val sunFull: String = "Sunday",
    val monFull: String = "Monday",
    val tueFull: String = "Tuesday",
    val wedFull: String = "Wednesday",
    val thuFull: String = "Thursday",
    val friFull: String = "Friday",
    val satFull: String = "Saturday",

    // Miscellaneous
    val upcoming: String = "Upcoming",
    val ongoing: String = "ONGOING",
    val none: String = "None",

    // Admin Panel & Sync
    val timeUpdated: String = "Time updated",
    val connect: String = "Connect",
    val saved: String = "Saved"
) {
    fun getMonthFullName(month: Int): String = when (month) {
        1 -> jan
        2 -> feb
        3 -> mar
        4 -> apr
        5 -> mayMonth
        6 -> jun
        7 -> jul
        8 -> aug
        9 -> sep
        10 -> oct
        11 -> nov
        12 -> dec
        else -> ""
    }

    fun getMonthShortName(month: Int): String = when (month) {
        1 -> janShort
        2 -> febShort
        3 -> marShort
        4 -> aprShort
        5 -> mayShort
        6 -> junShort
        7 -> julShort
        8 -> augShort
        9 -> sepShort
        10 -> octShort
        11 -> novShort
        12 -> decShort
        else -> ""
    }

    fun getDayOfWeekShort(dayOfWeek: Int): String = when (dayOfWeek) {
        java.util.Calendar.SUNDAY -> sun
        java.util.Calendar.MONDAY -> mon
        java.util.Calendar.TUESDAY -> tue
        java.util.Calendar.WEDNESDAY -> wed
        java.util.Calendar.THURSDAY -> thu
        java.util.Calendar.FRIDAY -> fri
        java.util.Calendar.SATURDAY -> sat
        else -> ""
    }

    fun getDayOfWeekFull(dayOfWeek: Int): String = when (dayOfWeek) {
        java.util.Calendar.SUNDAY -> sunFull
        java.util.Calendar.MONDAY -> monFull
        java.util.Calendar.TUESDAY -> tueFull
        java.util.Calendar.WEDNESDAY -> wedFull
        java.util.Calendar.THURSDAY -> thuFull
        java.util.Calendar.FRIDAY -> fri
        java.util.Calendar.SATURDAY -> satFull
        else -> ""
    }

    fun getDayHeadersList(): List<String> = listOf(sun, mon, tue, wed, thu, fri, sat)

    val title: String get() = boardTitle
    val ruleExpr: String get() = ptsPerPrayerSubtitle
    val missedSummary: String get() = missedTitle
}

val EnglishStrings = AppStrings()

val HindiStrings = AppStrings(
    // App Header
    solapur = "सोलापुर",
    azanTimeHeader = "अज़ान टाइम",
    punchLine = "आओ अल्लाह की राह में चलें",
    goToToday = "आज पर जाएँ",
    english = "अंग्रेज़ी",
    hindi = "हिंदी",
    urdu = "उर्दू",

    // Prayers
    fajr = "फ़ज्र",
    dhuhr = "ज़ोहर",
    jumah = "जुमा",
    asr = "असर",
    maghrib = "मग़रिब",
    isha = "ईशा",
    tahajjud = "तहज्जुद",
    azanLabel = "अज़ान",
    jammatLabel = "जमात",
    next = "अगला",
    markPrayed = "नमाज़ पढ़ी के रूप में मार्क करें",
    toggleAudio = "अज़ान आवाज़ बदलें",

    // Rak'ats Breakdown
    prayerType = "नमाज़ का प्रकार",
    rakats = "रकात",
    sunnat = "सुन्नत",
    farz = "फ़र्ज़",
    nafil = "नफ़िल",
    witr = "वित्र",
    total = "कुल",

    // Eid Cards
    eidUlFitr = "ईद-उल-फ़ितर",
    eidUlAdha = "ईद-उल-अज़हा",
    eidFitrDate = "09 मार्च 2027",
    eidFitrDay = "मंगलवार",
    eidAdhaDate = "16 मई 2027",
    eidAdhaDay = "रविवार",

    // Tracker Board Summary & Stats
    boardTitle = "365-दिन तक़वा",
    points = "तक़वा पॉइंट्स",
    ptsUnit = "अंक",
    ptsPerPrayerSubtitle = "प्रति नमाज़ 1 अंक",
    perfectDays = "पूर्ण दिन",
    fiveOfFiveSubtitle = "6/6 नमाज़ पढ़ी गई",
    streak = "लगातार दिन",
    daysUnit = "दिन",
    maxStreakSubtitle = "अधिकतम %d दिन",
    todaysPrayers = "आज की नमाज़ें",
    prayerStats = "आज के नमाज़ आँकड़े",
    namazTracker = "नमाज़",
    prayedTitle = "पढ़ी",
    missedTitle = "छूटी",
    close = "बंद करें",

    // Day Details Dialog
    todayLabel = "आज",
    yesterdayLabel = "कल",
    activeDateHint = "सक्रिय तिथि: नमाज़ पर टैप करें (प्रत्येक +1 अंक)",
    historicalRecordHint = "ऐतिहासिक रिकॉर्ड (केवल पढ़ने के लिए)",
    scheduledAt = "समय: %s",
    yetToPerform = "बाकी है",
    plusOnePoint = "+1 अंक",
    tapToMark = "मार्क करें",
    zeroPt = "0 अंक",
    totalPointsForDay = "दिन के कुल अंक",
    pointsOutOfFive = "%d / 6 अंक",

    // 1-Month History Calendar Accordion
    oneMonthHistory = "1-महीने का पॉइंट इतिहास",
    tapToExpand = "खोलने के लिए टैप करें",
    tapToCollapse = "बंद करने के लिए टैप करें",
    calendarRuleSubtitle = "प्रति नमाज़ 1 अंक • अधिकतम 6 अंक/दिन",
    legendZero = "0 (लाल)",
    legendOneToFour = "1-5",
    legendFive = "6 (सुनहरा)",
    legendToday = "आज",

    // Edit Prayer Points Section
    editPrayerPointsTitle = "नमाज़ पॉइंट्स संपादित करें (कल और आज)",
    yesterdaySimple = "कल",
    todaySimple = "आज",

    // Toast Messages
    prayerTimeNotArrivedToast = "%s का समय %s पर है। आप अज़ान के 20 मिनट बाद ही मार्क कर सकते हैं।",
    prayerTimeNotStartedToast = "%s की नमाज़ का समय अभी शुरू नहीं हुआ है।",

    // Zakat Calculator
    zakatCalcBtn = "ज़कात कैलकुलेटर",
    zakatCalcTitle = "ज़कात कैलकुलेटर",
    netWealthTitle = "कुल संपत्ति",
    payableZakatTitle = "देय ज़कात",

    // Ramazan 2027 Section
    ramazanTime2027 = "रमज़ान टाइम 2027",
    ramazanConcluded = "रमज़ान 2027 समाप्त हो चुका है।",
    ramazanDateAndDay = "तारीख व दिन",
    seharTime = "सहरी",
    iftarTime = "इफ्तार",
    roja = "रोज़ा",
    totalRozaPoints = "कुल रोज़ा अंक",
    cannotTickBeforeDateToast = "तारीख आने से पहले टिक नहीं कर सकते",
    rozaCompletedLabel = "मुकम्मल",
    rozaMissedLabel = "छूटा हुआ",
    todayBadge = "आज",

    // Ramazan Duas
    ramazanDuasHeading = "रमज़ान की दुआएं",
    duaLanguageLabel = "दुआ की भाषा",
    seharDuaTitle = "सेहरी की दुआ (रोज़ा रखने की नीयत)",
    seharDuaArabic = "وَبِصَوْمِ غَدٍ نَّوَيْتُ مِنْ شَهْرِ رَمَضَانَ",
    seharDuaTranslit = "व बिसौमि ग़दिन नवैतु मिन शहरि रमज़ान",
    seharDuaTranslation = "और मैंने रमज़ान के कल के रोज़े की नीयत की।",

    iftarDuaTitle = "इफ्तार की दुआ (रोज़ा खोलने की दुआ)",
    iftarDuaArabic = "اللَّهُمَّ إِنِّي لَكَ صُمْتُ وَبِكَ آمَنْتُ وَعَلَى رِزْقِكَ أَفْطَرْتُ",
    iftarDuaTranslit = "अल्लाहुम्मा इन्नी लका सुमतु व बिका आमन्तु व अला रिज़्क़िका अफ़्तरतु",
    iftarDuaTranslation = "ऐ अल्लाह! मैंने तेरे लिए रोज़ा रखा और तुझ पर ईमान लाया और तेरे दिए हुए रिज़्क़ से इफ़्तार किया।",

    // Months (Full)
    jan = "जनवरी",
    feb = "फ़रवरी",
    mar = "मार्च",
    apr = "अप्रैल",
    mayMonth = "मई",
    jun = "जून",
    jul = "जुलाई",
    aug = "अगस्त",
    sep = "सितंबर",
    oct = "अक्टूबर",
    nov = "नवंबर",
    dec = "दिसंबर",

    // Months (Short)
    janShort = "जन",
    febShort = "फ़र",
    marShort = "मार्च",
    aprShort = "अप्रैल",
    mayShort = "मई",
    junShort = "जून",
    julShort = "जुलाई",
    augShort = "अग",
    sepShort = "सितं",
    octShort = "अक्तू",
    novShort = "नवं",
    decShort = "दिसं",

    // Days of Week (Short Headers)
    sun = "रवि",
    mon = "सोम",
    tue = "मंगल",
    wed = "बुध",
    thu = "गुरु",
    fri = "शुक्र",
    sat = "शनि",

    // Days of Week (Full)
    sunFull = "रविवार",
    monFull = "सोमवार",
    tueFull = "मंगलवार",
    wedFull = "बुधवार",
    thuFull = "गुरुवार",
    friFull = "शुक्रवार",
    satFull = "शनिवार",

    // Miscellaneous
    upcoming = "आगामी",
    ongoing = "जारी",
    none = "कोई नहीं",

    // Admin Panel & Sync
    timeUpdated = "टाइम अपडेट हो गया",
    connect = "कनेक्ट",
    saved = "सेव्ड"
)

val UrduStrings = AppStrings(
    // App Header
    solapur = "سولاپور",
    azanTimeHeader = "اذان ٹائم",
    punchLine = "آؤ اللہ کی راہ میں چلیں",
    goToToday = "آج پر جائیں",
    english = "انگریزی",
    hindi = "ہندی",
    urdu = "اردو",

    // Prayers
    fajr = "فجر",
    dhuhr = "ظہر",
    jumah = "جمعہ",
    asr = "عصر",
    maghrib = "مغرب",
    isha = "عشاء",
    tahajjud = "تہجد",
    azanLabel = "اذان",
    jammatLabel = "جماعت",
    next = "اگلا",
    markPrayed = "پڑھی ہوئی کے طور پر نشان لگائیں",
    toggleAudio = "اذان آواز تبدیل کریں",

    // Rak'ats Breakdown
    prayerType = "نماز کی قسم",
    rakats = "رکعتیں",
    sunnat = "سنت",
    farz = "فرض",
    nafil = "نفل",
    witr = "وتر",
    total = "کل",

    // Eid Cards
    eidUlFitr = "عید الفطر",
    eidUlAdha = "عید الاضحی",
    eidFitrDate = "09 مارچ 2027",
    eidFitrDay = "منگل",
    eidAdhaDate = "16 مئی 2027",
    eidAdhaDay = "اتوار",

    // Tracker Board Summary & Stats
    boardTitle = "365 روزہ تقویٰ",
    points = "تقوی پوائنٹس",
    ptsUnit = "پوائنٹس",
    ptsPerPrayerSubtitle = "فی نماز 1 پوائنٹ",
    perfectDays = "مکمل دن",
    fiveOfFiveSubtitle = "6/6 نمازیں ادا کیں",
    streak = "مسلسل دن",
    daysUnit = "دن",
    maxStreakSubtitle = "زیادہ سے زیادہ %d دن",
    todaysPrayers = "آج کی نمازیں",
    prayerStats = "آج کی نماز کے اعداد و شمار",
    namazTracker = "نماز",
    prayedTitle = "پڑھی",
    missedTitle = "قضا",
    close = "بند کریں",

    // Day Details Dialog
    todayLabel = "آج",
    yesterdayLabel = "گزشتہ کل",
    activeDateHint = "فعال تاریخ: نماز پر ٹیپ کریں (ہر ایک +1 پوائنٹ)",
    historicalRecordHint = "تاریخی ریکارڈ (صرف پڑھنے کے لیے)",
    scheduledAt = "مقررہ وقت: %s",
    yetToPerform = "ادا کرنا باقی ہے",
    plusOnePoint = "+1 پوائنٹ",
    tapToMark = "نشان لگائیں",
    zeroPt = "0 پوائنٹ",
    totalPointsForDay = "دن کے کل پوائنٹس",
    pointsOutOfFive = "%d / 6 پوائنٹس",

    // 1-Month History Calendar Accordion
    oneMonthHistory = "1 ماہ کی پوائنٹ ہسٹری",
    tapToExpand = "دیکھنے کے لیے ٹیپ کریں",
    tapToCollapse = "بند کرنے کے لیے ٹیپ کریں",
    calendarRuleSubtitle = "فی نماز 1 پوائنٹ • زیادہ سے زیادہ 6 پوائنٹس/دن",
    legendZero = "0 (سرخ)",
    legendOneToFour = "1-5",
    legendFive = "6 (سنہری)",
    legendToday = "آج",

    // Edit Prayer Points Section
    editPrayerPointsTitle = "نماز پوائنٹس ایڈٹ کریں (کل اور آج)",
    yesterdaySimple = "کل",
    todaySimple = "آج",

    // Toast Messages
    prayerTimeNotArrivedToast = "%s کا وقت %s پر ہے۔ آپ اذان کے 20 منٹ بعد ہی نشان لگا سکتے ہیں۔",
    prayerTimeNotStartedToast = "%s کی نماز کا وقت ابھی شروع نہیں ہوا ہے۔",

    // Zakat Calculator
    zakatCalcBtn = "زکوٰۃ کیلکولیٹر",
    zakatCalcTitle = "زکوٰۃ کیلکولیٹر",
    netWealthTitle = "کل دولت",
    payableZakatTitle = "قابل ادائیگی زکوٰۃ",

    // Ramazan 2027 Section
    ramazanTime2027 = "رمضان ٹائم 2027",
    ramazanConcluded = "رمضان 2027 ختم ہو چکا ہے۔",
    ramazanDateAndDay = "تاریخ اور دن",
    seharTime = "سحری",
    iftarTime = "افطار",
    roja = "روزہ",
    totalRozaPoints = "کل روزہ پوائنٹس",
    cannotTickBeforeDateToast = "تاریخ آنے سے پہلے نشان نہیں لگا سکتے",
    rozaCompletedLabel = "مکمل",
    rozaMissedLabel = "چھوٹا ہوا",
    todayBadge = "آج",

    // Ramazan Duas
    ramazanDuasHeading = "رمضان کی دعائیں",
    duaLanguageLabel = "دعا کی زبان",
    seharDuaTitle = "سحری کی دعا (روزہ رکھنے کی نیت)",
    seharDuaArabic = "وَبِصَوْمِ غَدٍ نَّوَيْتُ مِنْ شَهْرِ رَمَضَانَ",
    seharDuaTranslit = "و ب صوم غد نویت من شہر رمضان",
    seharDuaTranslation = "اور میں نے ماہِ رمضان کے کل کے روزے کی نیت کی۔",

    iftarDuaTitle = "افطار کی دعا (روزہ کھولنے کی دعا)",
    iftarDuaArabic = "اللَّهُمَّ إِنِّي لَكَ صُمْتُ وَبِكَ آمَنْتُ وَعَلَى رِزْقِكَ أَفْطَرْتُ",
    iftarDuaTranslit = "اللھم انی لک صمت وبک امنت وعلی رزقک افطرت",
    iftarDuaTranslation = "اے اللہ! میں نے تیرے لیے روزہ رکھا اور تجھ پر ایمان لایا اور تیرے ہی دیے ہوئے رزق سے افطار کیا۔",

    // Months (Full)
    jan = "جنوری",
    feb = "فروری",
    mar = "مارچ",
    apr = "اپریل",
    mayMonth = "مئی",
    jun = "جون",
    jul = "جولائی",
    aug = "اگست",
    sep = "ستمبر",
    oct = "اکتوبر",
    nov = "نومبر",
    dec = "دسمبر",

    // Months (Short)
    janShort = "جنوری",
    febShort = "فروری",
    marShort = "مارچ",
    aprShort = "اپریل",
    mayShort = "مئی",
    junShort = "جون",
    julShort = "جولائی",
    augShort = "اگست",
    sepShort = "ستمبر",
    octShort = "اکتوبر",
    novShort = "نومبر",
    decShort = "دسمبر",

    // Days of Week (Short Headers)
    sun = "اتوار",
    mon = "پیر",
    tue = "منگل",
    wed = "بدھ",
    thu = "جمعرات",
    fri = "جمعہ",
    sat = "ہفتہ",

    // Days of Week (Full)
    sunFull = "اتوار",
    monFull = "پیر",
    tueFull = "منگل",
    wedFull = "بدھ",
    thuFull = "جمعرات",
    friFull = "جمعہ",
    satFull = "ہفتہ",

    // Miscellaneous
    upcoming = "آنے والا",
    ongoing = "جاری",
    none = "کوئی نہیں",

    // Admin Panel & Sync
    timeUpdated = "ٹائم اپڈیٹ ہو گیا",
    connect = "کنیکٹ",
    saved = "محفوظ"
)

val LocalAppStrings = staticCompositionLocalOf { EnglishStrings }
