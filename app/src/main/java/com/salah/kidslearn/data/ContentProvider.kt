package com.salah.kidslearn.data

/**
 * مزود المحتوى التعليمي - يحتوي على كل الحروف والأرقام والألوان والأشكال والحيوانات
 * مصمم للأطفال الجزائريين في مرحلة التحضيري (5-6 سنوات)
 */
object ContentProvider {

    // ================== الحروف العربية (28 حرف) ==================
    val arabicLetters: List<Letter> = listOf(
        Letter("ا", "ألف", "ألف", "أرنب", "Rabbit", "ic_animal_rabbit", "ar"),
        Letter("ب", "باء", "باء", "بطة", "Duck", "ic_animal_duck", "ar"),
        Letter("ت", "تاء", "تاء", "تفاحة", "Apple", "ic_fruit_apple", "ar"),
        Letter("ث", "ثاء", "ثاء", "ثعلب", "Fox", "ic_animal_fox", "ar"),
        Letter("ج", "جيم", "جيم", "جمل", "Camel", "ic_animal_camel", "ar"),
        Letter("ح", "حاء", "حاء", "حصان", "Horse", "ic_animal_horse", "ar"),
        Letter("خ", "خاء", "خاء", "خروف", "Sheep", "ic_animal_sheep", "ar"),
        Letter("د", "دال", "دال", "دب", "Bear", "ic_animal_bear", "ar"),
        Letter("ذ", "ذال", "ذال", "ذرة", "Corn", "ic_veggie_corn", "ar"),
        Letter("ر", "راء", "راء", "زرافة", "Giraffe", "ic_animal_giraffe", "ar"),
        Letter("ز", "زاي", "زاي", "زرزور", "Bird", "ic_animal_bird", "ar"),
        Letter("س", "سين", "سين", "سمكة", "Fish", "ic_animal_fish", "ar"),
        Letter("ش", "شين", "شين", "شمس", "Sun", "ic_nature_sun", "ar"),
        Letter("ص", "صاد", "صاد", "صقر", "Falcon", "ic_animal_falcon", "ar"),
        Letter("ض", "ضاد", "ضاد", "ضفدع", "Frog", "ic_animal_frog", "ar"),
        Letter("ط", "طاء", "طاء", "طائر", "Bird", "ic_animal_bird", "ar"),
        Letter("ظ", "ظاء", "ظاء", "ظبي", "Deer", "ic_animal_deer", "ar"),
        Letter("ع", "عين", "عين", "عصفور", "Sparrow", "ic_animal_bird", "ar"),
        Letter("غ", "غين", "غين", "غزال", "Gazelle", "ic_animal_deer", "ar"),
        Letter("ف", "فاء", "فاء", "فيل", "Elephant", "ic_animal_elephant", "ar"),
        Letter("ق", "قاف", "قاف", "قطة", "Cat", "ic_animal_cat", "ar"),
        Letter("ك", "كاف", "كاف", "كلب", "Dog", "ic_animal_dog", "ar"),
        Letter("ل", "لام", "لام", "ليمونة", "Lemon", "ic_fruit_lemon", "ar"),
        Letter("م", "ميم", "ميم", "موز", "Banana", "ic_fruit_banana", "ar"),
        Letter("ن", "نون", "نون", "نحلة", "Bee", "ic_animal_bee", "ar"),
        Letter("ه", "هاء", "هاء", "هلال", "Moon", "ic_nature_moon", "ar"),
        Letter("و", "واو", "واو", "وردة", "Rose", "ic_nature_rose", "ar"),
        Letter("ي", "ياء", "ياء", "يد", "Hand", "ic_body_hand", "ar")
    )

    // ================== الحروف الإنجليزية (26 حرف) ==================
    val englishLetters: List<Letter> = listOf(
        Letter("A", "A", "A", "Apple", "Apple", "ic_fruit_apple", "en"),
        Letter("B", "B", "B", "Ball", "Ball", "ic_toy_ball", "en"),
        Letter("C", "C", "C", "Cat", "Cat", "ic_animal_cat", "en"),
        Letter("D", "D", "D", "Dog", "Dog", "ic_animal_dog", "en"),
        Letter("E", "E", "E", "Elephant", "Elephant", "ic_animal_elephant", "en"),
        Letter("F", "F", "F", "Fish", "Fish", "ic_animal_fish", "en"),
        Letter("G", "G", "G", "Goat", "Goat", "ic_animal_goat", "en"),
        Letter("H", "H", "H", "House", "House", "ic_object_house", "en"),
        Letter("I", "I", "I", "Ice cream", "Ice cream", "ic_food_icecream", "en"),
        Letter("J", "J", "J", "Juice", "Juice", "ic_food_juice", "en"),
        Letter("K", "K", "K", "Kite", "Kite", "ic_toy_kite", "en"),
        Letter("L", "L", "L", "Lion", "Lion", "ic_animal_lion", "en"),
        Letter("M", "M", "M", "Moon", "Moon", "ic_nature_moon", "en"),
        Letter("N", "N", "N", "Nest", "Nest", "ic_object_nest", "en"),
        Letter("O", "O", "O", "Orange", "Orange", "ic_fruit_orange", "en"),
        Letter("P", "P", "P", "Pen", "Pen", "ic_object_pen", "en"),
        Letter("Q", "Q", "Q", "Queen", "Queen", "ic_person_queen", "en"),
        Letter("R", "R", "R", "Rabbit", "Rabbit", "ic_animal_rabbit", "en"),
        Letter("S", "S", "S", "Sun", "Sun", "ic_nature_sun", "en"),
        Letter("T", "T", "T", "Tiger", "Tiger", "ic_animal_tiger", "en"),
        Letter("U", "U", "U", "Umbrella", "Umbrella", "ic_object_umbrella", "en"),
        Letter("V", "V", "V", "Van", "Van", "ic_vehicle_van", "en"),
        Letter("W", "W", "W", "Whale", "Whale", "ic_animal_whale", "en"),
        Letter("X", "X", "X", "Xylophone", "Xylophone", "ic_object_xylophone", "en"),
        Letter("Y", "Y", "Y", "Yacht", "Yacht", "ic_vehicle_yacht", "en"),
        Letter("Z", "Z", "Z", "Zebra", "Zebra", "ic_animal_zebra", "en")
    )

    // ================== الأرقام (0-10) ==================
    val numbers: List<NumberItem> = listOf(
        NumberItem(0, "صفر", "Zero", "ic_count_zero"),
        NumberItem(1, "واحد", "One", "ic_count_one"),
        NumberItem(2, "اثنان", "Two", "ic_count_two"),
        NumberItem(3, "ثلاثة", "Three", "ic_count_three"),
        NumberItem(4, "أربعة", "Four", "ic_count_four"),
        NumberItem(5, "خمسة", "Five", "ic_count_five"),
        NumberItem(6, "ستة", "Six", "ic_count_six"),
        NumberItem(7, "سبعة", "Seven", "ic_count_seven"),
        NumberItem(8, "ثمانية", "Eight", "ic_count_eight"),
        NumberItem(9, "تسعة", "Nine", "ic_count_nine"),
        NumberItem(10, "عشرة", "Ten", "ic_count_ten")
    )

    // ================== الألوان الأساسية (7 ألوان) ==================
    val colors: List<ColorItem> = listOf(
        ColorItem("أحمر", "Red", "#E53935", "تفاحة", "ic_fruit_apple"),
        ColorItem("أصفر", "Yellow", "#FDD835", "شمس", "ic_nature_sun"),
        ColorItem("أزرق", "Blue", "#1E88E5", "سماء", "ic_nature_sky"),
        ColorItem("أخضر", "Green", "#43A047", "شجرة", "ic_nature_tree"),
        ColorItem("برتقالي", "Orange", "#FB8C00", "برتقالة", "ic_fruit_orange"),
        ColorItem("بنفسجي", "Purple", "#8E24AA", "عنب", "ic_fruit_grapes"),
        ColorItem("وردي", "Pink", "#EC407A", "وردة", "ic_nature_rose")
    )

    // ================== الأشكال الهندسية (6 أشكال) ==================
    val shapes: List<ShapeItem> = listOf(
        ShapeItem("دائرة", "Circle", "ic_shape_circle"),
        ShapeItem("مربع", "Square", "ic_shape_square"),
        ShapeItem("مثلث", "Triangle", "ic_shape_triangle"),
        ShapeItem("مستطيل", "Rectangle", "ic_shape_rectangle"),
        ShapeItem("نجمة", "Star", "ic_shape_star"),
        ShapeItem("قلب", "Heart", "ic_shape_heart")
    )

    // ================== الحيوانات (10 حيوانات) ==================
    val animals: List<AnimalItem> = listOf(
        AnimalItem("قطة", "Cat", "مياو", "ic_animal_cat"),
        AnimalItem("كلب", "Dog", "هو هو", "ic_animal_dog"),
        AnimalItem("أسد", "Lion", "زئير", "ic_animal_lion"),
        AnimalItem("بقرة", "Cow", "موه", "ic_animal_cow"),
        AnimalItem("حصان", "Horse", "صهيل", "ic_animal_horse"),
        AnimalItem("خروف", "Sheep", "ماع", "ic_animal_sheep"),
        AnimalItem("دجاجة", "Chicken", "كوكو", "ic_animal_chicken"),
        AnimalItem("بطة", "Duck", "كواك", "ic_animal_duck"),
        AnimalItem("سمكة", "Fish", "بلبل", "ic_animal_fish"),
        AnimalItem("فيل", "Elephant", "بوق", "ic_animal_elephant")
    )

    // ================== الشارات (Badges) ==================
    val badges: List<Badge> = listOf(
        Badge("first_letter", "أول حرف", "تعلمت أول حرف!", "ic_badge_first", 10),
        Badge("five_letters", "خمسة حروف", "تعلمت 5 حروف", "ic_badge_five", 50),
        Badge("alphabet_master", "أبجديّ", "أكملت الحروف كلها", "ic_badge_master", 260),
        Badge("number_rookie", "عدّاد", "تعلمت أول رقم", "ic_badge_counter", 280),
        Badge("color_blind", "فنان", "تعلمت كل الألوان", "ic_badge_artist", 350),
        Badge("zoo_keeper", "حارس الحديقة", "تعلمت كل الحيوانات", "ic_badge_zoo", 420),
        Badge("streak_7", "مثابر", "7 أيام متتالية", "ic_badge_streak", 100),
        Badge("streak_30", "بطل", "30 يوم متتالية", "ic_badge_champion", 500)
    )
}
