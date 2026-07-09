package com.example.urbanaura;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Controller
public class PageController {
    private static final String SESSION_USER = "currentUser";
    private static final String SESSION_AFTER_LOGIN = "afterLogin";
    private static final List<String> COUNTRY_ORDER = List.of(
            "france", "usa", "uk", "uae", "japan", "italy", "spain", "netherlands",
            "switzerland", "greece", "canada", "korea", "australia", "turkey", "germany", "portugal"
    );

    private final UserService userService;

    public PageController(UserService userService) {
        this.userService = userService;
    }

    private static final Map<String, Country> COUNTRIES = Map.ofEntries(
            entry("france", "Fransa", "Avrupa", "Lüks & Kültür", "Paris", "Euro", "france",
                    "Sanat, moda, gastronomi ve zarafetin buluştuğu Fransa; lüks ve kültürel yaşam tarzını sevenler için UrbanAura'nın en güçlü önerilerinden biri.",
                    items("Kruvasan", "Makaron", "Soğan Çorbası"), items("Eyfel Kulesi", "Louvre", "Versailles"), items("Paris Apartman Stili", "Klasik Dekorasyon", "Modern Minimal Evler"),
                    items("Trençkot", "Blazer", "Loafer"), items("Paris Moda Haftası", "Butik Mağazalar", "Lüks Markalar"), items("Sokak Kafeleri", "Kruvasan & Kahve", "Kitapçı Kafeler"),
                    items("Şık Restoranlar", "Rooftop Mekanlar", "Seine Nehri"), items("Fransız Rivierası", "Provence", "Bağ Evleri")),
            entry("usa", "ABD", "Amerika", "Modern & Dinamik", "New York", "Dolar", "usa",
                    "Hızlı şehir hayatı, güçlü kariyer olanakları ve modern yaşam enerjisiyle ABD dinamik bir ülke deneyimi sunar.",
                    items("Burger", "Cheesecake", "Pancake"), items("New York", "Los Angeles", "Grand Canyon"), items("Loft Daireler", "Modern Villalar", "Minimal Stüdyolar"),
                    items("Sneaker", "Denim", "Oversize Ceket"), items("Outlet Kültürü", "Tasarım Mağazaları", "Lüks Caddeler"), items("Kahve Zincirleri", "Brunch Mekanları", "Rooftop Cafeler"),
                    items("Broadway", "Rooftop Barlar", "Canlı Müzik"), items("Ulusal Parklar", "Sahil Şehirleri", "Göl Evleri")),
            entry("uk", "İngiltere", "Avrupa", "Tarih & Prestij", "Londra", "Sterlin", "uk",
                    "Tarihi dokusu, prestijli şehir yaşamı ve güçlü kültür sahnesiyle İngiltere klasik ve modern yaşamı birleştirir.",
                    items("Fish & Chips", "Scone", "Sunday Roast"), items("Big Ben", "British Museum", "Oxford"), items("Victoria Evleri", "Klasik Salonlar", "Modern Townhouse"),
                    items("Trençkot", "Oxford Ayakkabı", "Ekose Desen"), items("Savile Row", "Vintage Mağazalar", "Lüks Departmanlar"), items("Beş Çayı", "Kitapçı Kafeler", "Mahalle Pub Kafeleri"),
                    items("West End", "Şık Publar", "Caz Mekanları"), items("Cotswolds", "Lake District", "Kıyı Kasabaları")),
            entry("uae", "BAE", "Orta Doğu", "Lüks & Modern", "Dubai", "Dirhem", "uae",
                    "Gösterişli mimari, yüksek konfor ve modern şehir atmosferiyle BAE lüks yaşamı sevenler için güçlü bir seçenektir.",
                    items("Hurma Tatlıları", "Machboos", "Arap Kahvesi"), items("Burj Khalifa", "Dubai Marina", "Abu Dhabi"), items("Lüks Rezidanslar", "Panoramik Daireler", "Minimal Villalar"),
                    items("Şık Takımlar", "İpek Parçalar", "Altın Aksesuar"), items("Dubai Mall", "Lüks Markalar", "Tasarım Butikleri"), items("Hotel Lounge", "Çöl Kafeleri", "Marina Cafeleri"),
                    items("Sky Barlar", "Şık Restoranlar", "Marina Geceleri"), items("Çöl Safarisi", "Sahil Kulüpleri", "Palm Adaları")),
            entry("japan", "Japonya", "Asya", "Teknoloji & Gelenek", "Tokyo", "Yen", "japan",
                    "Japonya; teknoloji, düzen, minimalizm ve geleneksel kültürü aynı anda sunan etkileyici bir yaşam rotasıdır.",
                    items("Sushi", "Ramen", "Mochi"), items("Tokyo", "Kyoto", "Fuji Dağı"), items("Minimal Evler", "Tatami Odaları", "Akıllı Daireler"),
                    items("Minimal Kesimler", "Kimono Detayları", "Sneaker Stili"), items("Teknoloji Mağazaları", "Tasarım Butikleri", "Vintage Sokaklar"), items("Çay Evleri", "Kedi Kafeleri", "Minimal Cafeler"),
                    items("Tokyo Işıkları", "Karaoke", "İzakaya"), items("Bambu Ormanları", "Kaplıcalar", "Sakura Parkları")),
            entry("italy", "İtalya", "Avrupa", "Sanat & Lezzet", "Roma", "Euro", "italy",
                    "İtalya; tarihi şehirleri, güçlü yemek kültürü ve estetik yaşam tarzıyla sıcak ve zarif bir deneyim sunar.",
                    items("Pizza", "Pasta", "Tiramisu"), items("Roma", "Floransa", "Venedik"), items("Toskana Evleri", "Klasik İç Mekan", "Modern Daireler"),
                    items("Deri Ceket", "Loafer", "İpek Fular"), items("Milano Moda", "Deri Atölyeleri", "Butikler"), items("Espresso Bar", "Piazza Cafeleri", "Gelato Mekanları"),
                    items("Aperitivo", "Canlı Sokaklar", "Şarap Barları"), items("Toskana", "Amalfi", "Göl Kasabaları")),
            entry("spain", "İspanya", "Avrupa", "Sıcak & Sosyal", "Barcelona", "Euro", "spain",
                    "İspanya sosyal yaşamı, sıcak iklimi, renkli şehirleri ve Akdeniz hissiyle enerjik bir yaşam sunar.",
                    items("Paella", "Tapas", "Churros"), items("Barcelona", "Madrid", "Sevilla"), items("Akdeniz Evleri", "Renkli Seramikler", "Modern Sahil Daireleri"),
                    items("Keten Gömlek", "Renkli Elbise", "Rahat Sandalet"), items("Yerel Pazarlar", "Tasarım Butikleri", "Moda Caddeleri"), items("Tapas Cafeleri", "Sahil Kafeleri", "Meydan Mekanları"),
                    items("Flamenko", "Gece Meydanları", "Rooftop Barlar"), items("Costa Brava", "Adalar", "Zeytinlikler")),
            entry("netherlands", "Hollanda", "Avrupa", "Sade & Özgür", "Amsterdam", "Euro", "netherlands",
                    "Hollanda düzenli şehirleri, bisiklet kültürü ve sade modern yaşam anlayışıyla özgür bir atmosfer sunar.",
                    items("Stroopwafel", "Peynir", "Poffertjes"), items("Amsterdam", "Kanallar", "Van Gogh Müzesi"), items("Kanal Evleri", "Minimal Daireler", "Açık Plan Evler"),
                    items("Rahat Sneaker", "Yağmurluk", "Minimal Çanta"), items("Tasarım Mağazaları", "Çiçek Pazarları", "Vintage Dükkanlar"), items("Kanal Kafeleri", "Bisiklet Dostu Mekanlar", "Fırınlar"),
                    items("Kanal Geceleri", "Canlı Müzik", "Kültür Barları"), items("Lale Bahçeleri", "Sahil Kasabaları", "Yeşil Parklar")),
            entry("switzerland", "İsviçre", "Avrupa", "Doğa & Kalite", "Zürih", "Frank", "switzerland",
                    "İsviçre doğa, güven, kalite ve sakin lüks yaşamı bir araya getiren güçlü bir yaşam rotasıdır.",
                    items("Fondü", "Çikolata", "Rösti"), items("Zürih", "Luzern", "Alpler"), items("Dağ Evleri", "Modern Ahşap Evler", "Göl Manzaralı Daireler"),
                    items("Yün Kaban", "Minimal Bot", "Kaliteli Saat"), items("Saat Butikleri", "Çikolata Mağazaları", "Lüks Caddeler"), items("Göl Cafeleri", "Dağ Kafeleri", "Pastaneler"),
                    items("Şık Loungelar", "Göl Kenarı", "Sessiz Barlar"), items("Alpler", "Göller", "Kayak Rotaları")),
            entry("greece", "Yunanistan", "Avrupa", "Deniz & Tarih", "Atina", "Euro", "greece",
                    "Yunanistan beyaz-mavi mimarisi, Ege atmosferi, tarihi dokusu ve sakin tatil yaşamıyla öne çıkar.",
                    items("Souvlaki", "Moussaka", "Baklava"), items("Santorini", "Atina", "Mikonos"), items("Beyaz Taş Evler", "Ada Evleri", "Bohem Teraslar"),
                    items("Keten Elbise", "Sandalet", "Hasır Çanta"), items("Ada Butikleri", "Seramik Atölyeleri", "Yerel Pazarlar"), items("Deniz Kafeleri", "Ada Terasları", "Kahve Mekanları"),
                    items("Sahil Barları", "Canlı Tavarnalar", "Gün Batımı Mekanları"), items("Ege Adaları", "Mavi Koylar", "Zeytinlikler")),
            entry("canada", "Kanada", "Amerika", "Doğa & Güven", "Toronto", "Kanada Doları", "canada",
                    "Kanada doğa, güvenli şehir yaşamı ve ferah yaşam alanları arayanlar için dengeli bir ülke deneyimi sunar.",
                    items("Poutine", "Maple Tatlısı", "Somon"), items("Toronto", "Vancouver", "Banff"), items("Ahşap Evler", "Modern Apartman", "Göl Evleri"),
                    items("Puffer Mont", "Outdoor Bot", "Rahat Denim"), items("Yerel Markalar", "Outdoor Mağazaları", "Alışveriş Caddeleri"), items("Brunch Cafeleri", "Dağ Kafeleri", "Kitapçı Cafeler"),
                    items("Canlı Müzik", "Şehir Barları", "Kış Festivalleri"), items("Ulusal Parklar", "Göller", "Kayak Merkezleri")),
            entry("korea", "Güney Kore", "Asya", "Teknoloji & Enerji", "Seul", "Won", "korea",
                    "Güney Kore hızlı şehir hayatı, K-kültürü, teknoloji ve güçlü sokak stilini bir araya getirir.",
                    items("Kimbap", "Bibimbap", "Tteokbokki"), items("Seul", "Busan", "Gyeongbokgung"), items("Akıllı Daireler", "Minimal Odalar", "Modern Stüdyolar"),
                    items("Streetwear", "Oversize Ceket", "Sneaker"), items("K-Beauty", "Moda Sokakları", "Tasarım Mağazaları"), items("Temalı Cafeler", "Dessert Cafeler", "Study Cafe"),
                    items("Hongdae", "Karaoke", "Gece Pazarları"), items("Jeju", "Dağ Rotaları", "Sahil Yolları")),
            entry("australia", "Avustralya", "Okyanusya", "Sahil & Özgürlük", "Sydney", "Avustralya Doları", "australia",
                    "Avustralya sahil yaşamı, modern şehirleri ve özgür açık hava kültürüyle enerjik bir yaşam sunar.",
                    items("Avocado Toast", "Barbekü", "Lamington"), items("Sydney", "Melbourne", "Great Barrier Reef"), items("Sahil Evleri", "Modern Loft", "Açık Plan Evler"),
                    items("Rahat Keten", "Sandalet", "Surf Stili"), items("Tasarım Pazarları", "Yerel Markalar", "Sahil Butikleri"), items("Brunch Cafeleri", "Sahil Cafeleri", "Kahve Mekanları"),
                    items("Rooftop Barlar", "Sahil Partileri", "Canlı Müzik"), items("Plajlar", "Mercan Resifi", "Ulusal Parklar")),
            entry("turkey", "Türkiye", "Avrupa & Asya", "Kültür & Sıcak Yaşam", "İstanbul", "Türk Lirası", "turkey",
                    "Türkiye zengin tarihi, sıcak yaşam kültürü, güçlü mutfağı ve farklı coğrafyalarıyla çok yönlü bir deneyim sunar.",
                    items("Kebap", "Baklava", "Türk Kahvesi"), items("İstanbul", "Kapadokya", "Pamukkale"), items("Taş Evler", "Bohem Sahil Evleri", "Modern Şehir Daireleri"),
                    items("Rahat Şıklık", "Akdeniz Tonları", "Modern Şehir Stili"), items("Kapalıçarşı", "Tasarım Butikleri", "Yerel Pazarlar"), items("Boğaz Kafeleri", "Kitapçı Kafeler", "Sahil Mekanları"),
                    items("Meyhaneler", "Rooftoplar", "Canlı Müzik"), items("Ege Koyları", "Kapadokya", "Yaylalar")),
            entry("germany", "Almanya", "Avrupa", "Düzen & Kariyer", "Berlin", "Euro", "germany",
                    "Almanya düzenli şehir yaşamı, kariyer olanakları, modern mimari ve güçlü kültür sahnesiyle öne çıkar.",
                    items("Pretzel", "Schnitzel", "Strudel"), items("Berlin", "Münih", "Köln"), items("Modern Daireler", "Endüstriyel Loft", "Bahçeli Evler"),
                    items("Minimal Ceket", "Sneaker", "Pratik Çanta"), items("Tasarım Mağazaları", "Yerel Pazarlar", "Alışveriş Caddeleri"), items("Üçüncü Nesil Kahve", "Fırın Cafeleri", "Kitap Kafeler"),
                    items("Berlin Kulüpleri", "Bira Bahçeleri", "Konserler"), items("Bavyera", "Orman Rotaları", "Göller")),
            entry("portugal", "Portekiz", "Avrupa", "Okyanus & Huzur", "Lizbon", "Euro", "portugal",
                    "Portekiz okyanus manzaraları, sıcak sokakları, sakin yaşam temposu ve estetik şehirleriyle huzurlu bir rota sunar.",
                    items("Pastel de Nata", "Bacalhau", "Deniz Ürünleri"), items("Lizbon", "Porto", "Algarve"), items("Azulejo Evler", "Sahil Daireleri", "Taş Evler"),
                    items("Keten Gömlek", "Hasır Çanta", "Rahat Ayakkabı"), items("Seramik Atölyeleri", "Yerel Butikler", "Tasarım Pazarları"), items("Pastane Cafeleri", "Sahil Cafeleri", "Teras Mekanları"),
                    items("Fado Geceleri", "Şarap Barları", "Sahil Mekanları"), items("Algarve", "Douro Vadisi", "Okyanus Rotaları"))
    );

    private static Map.Entry<String, Country> entry(String slug, String name, String region, String lifestyle,
                                                    String highlight, String currency, String imageClass,
                                                    String description, List<String> foods, List<String> places,
                                                    List<String> homes, List<String> styles, List<String> shopping,
                                                    List<String> cafes, List<String> nightlife, List<String> nature) {
        return Map.entry(slug, new Country(slug, name, region, lifestyle, highlight, currency, imageClass, description,
                guide(name, foods, places, homes, styles, shopping, cafes, nightlife, nature)));
    }

    private static List<String> items(String first, String second, String third) {
        return List.of(first, second, third);
    }

    private static List<GuideSection> guide(String countryName, List<String> foods, List<String> places, List<String> homes,
                                            List<String> styles, List<String> shopping, List<String> cafes,
                                            List<String> nightlife, List<String> nature) {
        return List.of(
                section(countryName, "foods", "LEZZET", "Meşhur Yemekler", "Ülkenin mutfağını temsil eden en bilinen lezzetler ve yerel tatlar.", foods),
                section(countryName, "places", "ROTA", "Meşhur Yerler", "Ülkenin simge yapıları, şehirleri ve en çok ziyaret edilen özel rotaları.", places),
                section(countryName, "homes", "YAŞAM", "Ev Tasarımları", "Ev dekorasyonu, mimari detaylar ve ülkenin yaşam alanlarına yansıyan estetik anlayışı.", homes),
                section(countryName, "street-style", "STİL", "Sokak Tarzı", "Günlük giyimde öne çıkan parçalar, renkler ve şehir stilinin karakteri.", styles),
                section(countryName, "fashion", "MODA", "Alışveriş & Moda", "Butikler, yerel tasarım kültürü, marka deneyimleri ve alışveriş rotaları.", shopping),
                section(countryName, "cafes", "KAFE", "Kafe Kültürü", "Kahve, tatlı, sohbet ve şehir yaşamının en keyifli durakları.", cafes),
                section(countryName, "nightlife", "GECE", "Gece Hayatı", "Akşam yemekleri, müzik, manzara ve şehir ışıklarıyla oluşan gece atmosferi.", nightlife),
                section(countryName, "holiday", "TATİL", "Doğa & Tatil", "Sahil, kırsal rota, doğa kaçışı ve sakin tatil seçenekleri.", nature)
        );
    }

    private static GuideSection section(String countryName, String id, String eyebrow, String title,
                                        String description, List<String> itemNames) {
        List<GalleryItem> items = List.of(
                galleryItem(countryName, itemNames.get(0), title, id),
                galleryItem(countryName, itemNames.get(1), title, id),
                galleryItem(countryName, itemNames.get(2), title, id)
        );

        return new GuideSection(id, eyebrow, title, description, items.get(0).image(), countryName + " " + title, items);
    }

    private static GalleryItem galleryItem(String countryName, String itemName, String title, String id) {
        return new GalleryItem(itemName, itemText(countryName, itemName, title), imageUrl(countryName, itemName, id));
    }

    private static String imageUrl(String countryName, String itemName, String sectionId) {
        String fixedImage = fixedImage(countryName, itemName, sectionId);
        if (fixedImage != null) {
            return fixedImage;
        }

        return fallbackImage(countryName);
    }

    private static String imageQuery(String countryName, String itemName, String sectionId) {
        String explicit = switch (itemName) {
            case "Hurma Tatlıları" -> "dates,dessert";
            case "Machboos" -> "arabic,rice,food";
            case "Arap Kahvesi" -> "arabic,coffee";
            case "Burj Khalifa" -> "burj,khalifa,dubai";
            case "Dubai Marina" -> "dubai,marina";
            case "Abu Dhabi" -> "abu,dhabi,mosque";
            case "Lüks Rezidanslar" -> "luxury,residence,interior";
            case "Panoramik Daireler" -> "penthouse,apartment,view";
            case "Minimal Villalar" -> "minimal,villa,interior";
            case "Şık Takımlar" -> "elegant,suit,fashion";
            case "İpek Parçalar" -> "silk,fashion";
            case "Altın Aksesuar" -> "gold,jewelry";
            case "Dubai Mall" -> "dubai,mall,shopping";
            case "Hotel Lounge" -> "hotel,lounge,luxury";
            case "Çöl Kafeleri" -> "desert,cafe";
            case "Marina Cafeleri" -> "marina,cafe";
            case "Sky Barlar" -> "sky,bar,city";
            case "Marina Geceleri" -> "marina,night";
            case "Çöl Safarisi" -> "desert,safari";
            case "Sahil Kulüpleri" -> "beach,club";
            case "Palm Adaları" -> "palm,jumeirah";

            case "Sushi" -> "sushi";
            case "Ramen" -> "ramen";
            case "Mochi" -> "mochi";
            case "Tokyo" -> "tokyo,city";
            case "Kyoto" -> "kyoto,temple";
            case "Fuji Dağı" -> "mount,fuji";
            case "Minimal Evler" -> "japanese,minimal,home";
            case "Tatami Odaları" -> "tatami,room";
            case "Akıllı Daireler" -> "smart,apartment";
            case "Minimal Kesimler" -> "minimal,fashion";
            case "Kimono Detayları" -> "kimono";
            case "Sneaker Stili" -> "sneaker,streetwear";
            case "Teknoloji Mağazaları" -> "technology,store";
            case "Vintage Sokaklar" -> "vintage,street";
            case "Çay Evleri" -> "japanese,tea,house";
            case "Kedi Kafeleri" -> "cat,cafe";
            case "Minimal Cafeler" -> "minimal,cafe";
            case "Tokyo Işıkları" -> "tokyo,night";
            case "Karaoke" -> "karaoke";
            case "İzakaya" -> "izakaya";
            case "Bambu Ormanları" -> "bamboo,forest";
            case "Kaplıcalar" -> "onsen";
            case "Sakura Parkları" -> "sakura,park";

            case "Kebap" -> "kebab";
            case "Baklava" -> "baklava";
            case "Türk Kahvesi" -> "turkish,coffee";
            case "İstanbul" -> "istanbul";
            case "Kapadokya" -> "cappadocia";
            case "Pamukkale" -> "pamukkale";
            case "Taş Evler" -> "stone,house";
            case "Bohem Sahil Evleri" -> "bohemian,beach,house";
            case "Modern Şehir Daireleri" -> "modern,city,apartment";
            case "Rahat Şıklık" -> "casual,elegant,fashion";
            case "Akdeniz Tonları" -> "mediterranean,fashion";
            case "Modern Şehir Stili" -> "modern,city,style";
            case "Kapalıçarşı" -> "grand,bazaar,istanbul";
            case "Yerel Pazarlar" -> "local,market";
            case "Boğaz Kafeleri" -> "bosphorus,cafe";
            case "Sahil Mekanları" -> "seaside,restaurant";
            case "Meyhaneler" -> "tavern,restaurant";
            case "Rooftoplar" -> "rooftop,bar";
            case "Ege Koyları" -> "aegean,bay";
            case "Yaylalar" -> "mountain,plateau";

            default -> null;
        };

        if (explicit != null) {
            return explicit;
        }

        return countryName + " " + itemName + " " + switch (sectionId) {
            case "foods" -> "food";
            case "places" -> "landmark";
            case "homes" -> "interior design";
            case "street-style" -> "street style";
            case "fashion" -> "shopping fashion";
            case "cafes" -> "cafe";
            case "nightlife" -> "nightlife";
            case "holiday" -> "nature travel";
            default -> "travel";
        };
    }

    private static String fixedImage(String countryName, String itemName, String sectionId) {
        if ("Fransa".equals(countryName)) {
            return switch (itemName) {
                case "Kruvasan" -> "/images/france-croissant.png";
                case "Makaron" -> "/images/france-macaron.png";
                case "Soğan Çorbası" -> "/images/france-onion-soup.png";
                case "Eyfel Kulesi" -> "/images/paris-luxury-night.png";
                case "Louvre" -> "/images/france-louvre.png";
                case "Versailles" -> "/images/france-classic-streets.png";
                case "Paris Apartman Stili" -> "/images/france-paris-apartment.png";
                case "Klasik Dekorasyon" -> "/images/france-classic-interior.png";
                case "Modern Minimal Evler" -> "/images/france-minimal-home.png";
                case "Trençkot" -> "/images/france-trench.png";
                case "Blazer" -> "/images/france-blazer.png";
                case "Loafer" -> "/images/france-loafer.png";
                case "Paris Moda Haftası" -> "/images/france-fashion-week.png";
                case "Butik Mağazalar" -> "/images/france-boutiques.png";
                case "Lüks Markalar" -> "/images/france-luxury-brands.png";
                case "Sokak Kafeleri" -> "/images/france-street-cafes.png";
                case "Kruvasan & Kahve" -> "/images/france-croissant-coffee.png";
                case "Kitapçı Kafeler" -> "/images/france-bookstore-cafes.png";
                case "Şık Restoranlar" -> "/images/france-elegant-restaurants.png";
                case "Rooftop Mekanlar" -> "/images/france-rooftop-venues.png";
                case "Seine Nehri" -> "/images/france-seine.png";
                case "Fransız Rivierası" -> "/images/france-riviera.png";
                case "Provence" -> "/images/france-vineyard-houses.png";
                case "Bağ Evleri" -> "/images/france-vineyard-homes.png";
                default -> null;
            };
        }

        if ("İsviçre".equals(countryName)) {
            return switch (itemName) {
                case "Fondü" -> "/images/switzerland-fondue.png";
                case "Çikolata" -> "/images/switzerland-chocolate.png";
                case "Rösti" -> "/images/switzerland-chocolate-shops.png";
                case "Zürih" -> "/images/switzerland-zurich.png";
                case "Luzern" -> "/images/switzerland-lucerne.png";
                case "Alpler" -> "/images/switzerland-alps.png";
                case "Göller" -> "/images/switzerland-lakes.png";
                case "Kayak Rotaları" -> "/images/switzerland-ski-routes.png";
                case "Dağ Evleri" -> "/images/switzerland-chalets.png";
                case "Modern Ahşap Evler" -> "/images/switzerland-modern-wood-homes.png";
                case "Göl Manzaralı Daireler" -> "/images/switzerland-lake-apartments.png";
                case "Yün Kaban" -> "/images/switzerland-wool-coat.png";
                case "Minimal Bot" -> "/images/switzerland-minimal-boots.png";
                case "Kaliteli Saat" -> "/images/switzerland-luxury-watch.png";
                case "Saat Butikleri" -> "/images/switzerland-watch-boutiques.png";
                case "Çikolata Mağazaları" -> "/images/switzerland-chocolate-shops.png";
                case "Lüks Caddeler" -> "/images/switzerland-luxury-streets.png";
                case "Göl Cafeleri" -> "/images/switzerland-lake-cafes.png";
                case "Dağ Kafeleri" -> "/images/switzerland-mountain-cafes.png";
                case "Pastaneler" -> "/images/switzerland-patisseries.png";
                case "Şık Loungelar" -> "/images/switzerland-chic-lounges.png";
                case "Göl Kenarı" -> "/images/switzerland-lakeside.png";
                case "Sessiz Barlar" -> "/images/switzerland-quiet-bars.png";
                default -> null;
            };
        }

        if ("Yunanistan".equals(countryName)) {
            return switch (itemName) {
                case "Souvlaki" -> "/images/greece-souvlaki.png";
                case "Moussaka" -> "/images/greece-moussaka.png";
                case "Baklava" -> "/images/greece-baklava.png";
                case "Santorini" -> "/images/greece-santorini.png";
                case "Atina" -> "/images/greece-athens.png";
                case "Mikonos" -> "/images/greece-mykonos.png";
                case "Beyaz Taş Evler" -> "/images/greece-white-stone-houses.png";
                case "Ada Evleri" -> "/images/greece-island-houses.png";
                case "Bohem Teraslar" -> "/images/greece-bohemian-terraces.png";
                case "Keten Elbise" -> "/images/greece-linen-dress.png";
                case "Sandalet" -> "/images/greece-sandals.png";
                case "Hasır Çanta" -> "/images/greece-straw-bag.png";
                case "Ada Butikleri" -> "/images/greece-island-boutiques.png";
                case "Seramik Atölyeleri" -> "/images/greece-ceramic-workshops.png";
                case "Yerel Pazarlar" -> "/images/greece-local-markets.png";
                case "Deniz Kafeleri" -> "/images/greece-sea-cafes.png";
                case "Ada Terasları" -> "/images/greece-island-terraces.png";
                case "Kahve Mekanları" -> "/images/greece-coffee-places.png";
                case "Sahil Barları" -> "/images/greece-beach-bars.png";
                case "Canlı Tavarnalar" -> "/images/greece-live-taverns.png";
                case "Gün Batımı Mekanları" -> "/images/greece-sunset-places.png";
                case "Mavi Koylar" -> "/images/greece-blue-bays.png";
                case "Zeytinlikler" -> "/images/greece-olive-groves.png";
                default -> null;
            };
        }

        if ("Kanada".equals(countryName)) {
            return switch (itemName) {
                case "Poutine" -> "/images/canada-poutine.png";
                case "Maple Tatlısı" -> "/images/canada-maple-dessert.png";
                case "Somon" -> "/images/canada-salmon.png";
                case "Toronto" -> "/images/canada-toronto.png";
                case "Vancouver" -> "/images/canada-vancouver.png";
                case "Banff" -> "/images/canada-banff.png";
                case "Ahşap Evler" -> "/images/canada-wooden-houses.png";
                case "Modern Apartman" -> "/images/canada-modern-apartment.png";
                case "Göl Evleri" -> "/images/canada-lake-houses.png";
                case "Puffer Mont" -> "/images/canada-puffer-coat.png";
                case "Outdoor Bot" -> "/images/canada-outdoor-boots.png";
                case "Rahat Denim" -> "/images/canada-relaxed-denim.png";
                case "Yerel Markalar" -> "/images/canada-local-brands.png";
                case "Outdoor Mağazaları" -> "/images/canada-outdoor-stores.png";
                case "Alışveriş Caddeleri" -> "/images/canada-shopping-streets.png";
                case "Brunch Cafeleri" -> "/images/canada-brunch-cafes.png";
                case "Dağ Kafeleri" -> "/images/canada-mountain-cafes.png";
                case "Kitapçı Cafeler" -> "/images/canada-bookstore-cafes.png";
                case "Canlı Müzik" -> "/images/canada-live-music.png";
                case "Şehir Barları" -> "/images/canada-city-bars.png";
                case "Kış Festivalleri" -> "/images/canada-winter-festivals.png";
                case "Ulusal Parklar" -> "/images/canada-national-parks.png";
                case "Göller" -> "/images/canada-lakes.png";
                case "Kayak Merkezleri" -> "/images/canada-ski-resorts.png";
                default -> null;
            };
        }

        if ("Güney Kore".equals(countryName)) {
            return switch (itemName) {
                case "Kimbap" -> "/images/korea-kimbap.png";
                case "Bibimbap" -> "/images/korea-bibimbap.png";
                case "Tteokbokki" -> "/images/korea-tteokbokki.png";
                case "Seul" -> "/images/korea-seoul.png";
                case "Busan" -> "/images/korea-busan.png";
                case "Gyeongbokgung" -> "/images/korea-gyeongbokgung.png";
                case "Akıllı Daireler" -> "/images/korea-smart-apartments.png";
                case "Minimal Odalar" -> "/images/korea-minimal-rooms.png";
                case "Modern Stüdyolar" -> "/images/korea-modern-studios.png";
                case "Streetwear" -> "/images/korea-streetwear.png";
                case "Oversize Ceket" -> "/images/korea-oversize-jacket.png";
                case "Sneaker" -> "/images/korea-sneaker.png";
                case "K-Beauty" -> "/images/korea-k-beauty.png";
                case "Moda Sokakları" -> "/images/korea-fashion-streets.png";
                case "Tasarım Mağazaları" -> "/images/korea-design-stores.png";
                case "Temalı Cafeler" -> "/images/korea-themed-cafes.png";
                case "Dessert Cafeler" -> "/images/korea-dessert-cafes.png";
                case "Study Cafe" -> "/images/korea-study-cafe.png";
                case "Hongdae" -> "/images/korea-hongdae.png";
                case "Karaoke" -> "/images/korea-karaoke.png";
                case "Gece Pazarları" -> "/images/korea-night-markets.png";
                case "Jeju" -> "/images/korea-jeju.png";
                case "Dağ Rotaları" -> "/images/korea-mountain-routes.png";
                case "Sahil Yolları" -> "/images/korea-coastal-roads.png";
                default -> null;
            };
        }

        if ("ABD".equals(countryName)) {
            return switch (itemName) {
                case "Burger" -> "/images/usa-burger.png";
                case "Cheesecake" -> "/images/usa-cheesecake.png";
                case "Pancake" -> "/images/usa-pancake.png";
                case "New York" -> "/images/usa-new-york.png";
                case "Los Angeles" -> "/images/usa-coastal-cities.png";
                case "Grand Canyon" -> "/images/usa-national-parks.png";
                case "Loft Daireler" -> "/images/usa-loft-apartments.png";
                case "Modern Villalar" -> "/images/usa-modern-villas.png";
                case "Minimal Stüdyolar" -> "/images/usa-minimal-studios.png";
                case "Sneaker" -> "/images/usa-sneaker.png";
                case "Denim" -> "/images/usa-denim.png";
                case "Oversize Ceket" -> "/images/usa-oversize-jacket.png";
                case "Outlet Kültürü" -> "/images/usa-outlet-culture.png";
                case "Tasarım Mağazaları" -> "/images/usa-design-stores.png";
                case "Lüks Caddeler" -> "/images/usa-luxury-streets.png";
                case "Kahve Zincirleri" -> "/images/usa-coffee-chains.png";
                case "Brunch Mekanları" -> "/images/usa-brunch-spots.png";
                case "Rooftop Cafeler" -> "/images/usa-rooftop-cafes.png";
                case "Broadway" -> "/images/usa-broadway.png";
                case "Rooftop Barlar" -> "/images/usa-rooftop-bars.png";
                case "Canlı Müzik" -> "/images/usa-live-music.png";
                case "Ulusal Parklar" -> "/images/usa-national-parks.png";
                case "Sahil Şehirleri" -> "/images/usa-coastal-cities.png";
                case "Göl Evleri" -> "/images/usa-lake-houses.png";
                default -> null;
            };
        }

        if ("BAE".equals(countryName)) {
            return switch (itemName) {
                case "Hurma Tatlıları" -> "/images/uae-date-desserts.png";
                case "Machboos" -> "/images/uae-machboos.png";
                case "Arap Kahvesi" -> "/images/uae-arabic-coffee.png";
                case "Burj Khalifa" -> "/images/uae-burj-khalifa.png";
                case "Dubai Marina" -> "/images/uae-dubai-marina.png";
                case "Abu Dhabi" -> "/images/uae-abu-dhabi.png";
                case "Lüks Rezidanslar" -> "/images/uae-luxury-residences.png";
                case "Panoramik Daireler" -> "/images/uae-panoramic-apartments.png";
                case "Minimal Villalar" -> "/images/uae-minimal-villas.png";
                case "Şık Takımlar" -> "/images/uae-elegant-suits.png";
                case "İpek Parçalar" -> "/images/uae-silk-pieces.png";
                case "Altın Aksesuar" -> "/images/uae-gold-accessories.png";
                case "Dubai Mall" -> "/images/uae-dubai-mall.png";
                case "Lüks Markalar" -> "/images/uae-luxury-brands.png";
                case "Tasarım Butikleri" -> "/images/uae-design-boutiques.png";
                case "Hotel Lounge" -> "/images/uae-hotel-lounge.png";
                case "Çöl Kafeleri" -> "/images/uae-desert-cafes.png";
                case "Marina Cafeleri" -> "/images/uae-marina-cafes.png";
                case "Sky Barlar" -> "/images/uae-sky-bars.png";
                case "Şık Restoranlar" -> "/images/uae-elegant-restaurants.png";
                case "Marina Geceleri" -> "/images/uae-marina-nights.png";
                case "Çöl Safarisi" -> "/images/uae-desert-safari.png";
                case "Sahil Kulüpleri" -> "/images/uae-beach-clubs.png";
                case "Palm Adaları" -> "/images/uae-palm-islands.png";
                default -> null;
            };
        }

        if ("İngiltere".equals(countryName)) {
            return switch (itemName) {
                case "Fish & Chips" -> "/images/uk-fish-and-chips.png";
                case "Scone" -> "/images/uk-afternoon-tea.png";
                case "Sunday Roast" -> "/images/uk-sunday-roast.png";
                case "Big Ben" -> "/images/uk-big-ben.png";
                case "British Museum" -> "/images/uk-british-museum.png";
                case "Oxford" -> "/images/uk-oxford.png";
                case "Victoria Evleri" -> "/images/uk-victorian-houses.png";
                case "Klasik Salonlar" -> "/images/uk-classic-salons.png";
                case "Modern Townhouse" -> "/images/uk-modern-townhouse.png";
                case "Trençkot" -> "/images/uk-trench.png";
                case "Oxford Ayakkabı" -> "/images/uk-oxford-shoes.png";
                case "Ekose Desen" -> "/images/uk-savile-row.png";
                case "Savile Row" -> "/images/uk-savile-row.png";
                case "Vintage Mağazalar" -> "/images/uk-vintage-shops.png";
                case "Lüks Departmanlar" -> "/images/uk-luxury-departments.png";
                case "Beş Çayı" -> "/images/uk-afternoon-tea.png";
                case "Kitapçı Kafeler" -> "/images/uk-bookstore-cafes.png";
                case "Mahalle Pub Kafeleri" -> "/images/uk-neighborhood-pub-cafes.png";
                case "West End" -> "/images/uk-west-end.png";
                case "Şık Publar" -> "/images/uk-stylish-pubs.png";
                case "Caz Mekanları" -> "/images/uk-jazz-venues.png";
                case "Cotswolds" -> "/images/uk-cotswolds.png";
                case "Lake District" -> "/images/uk-lake-district.png";
                case "Kıyı Kasabaları" -> "/images/uk-coastal-villages.png";
                default -> null;
            };
        }

        if ("İtalya".equals(countryName)) {
            return switch (itemName) {
                case "Pizza" -> "/images/italy-pizza.png";
                case "Pasta" -> "/images/italy-pasta.png";
                case "Tiramisu" -> "/images/italy-tiramisu.png";
                case "Roma" -> "/images/italy-roma.png";
                case "Floransa" -> "/images/italy-floransa.png";
                case "Venedik" -> "/images/italy-venedik.png";
                case "Toskana Evleri" -> "/images/italy-toskana-evleri.png";
                case "Toskana" -> "/images/italy-toskana.png";
                case "Klasik İç Mekan" -> "/images/italy-klasik-ic-mekan.png";
                case "Modern Daireler" -> "/images/italy-modern-daireler.png";
                case "Deri Ceket" -> "/images/italy-deri-ceket.png";
                case "Loafer" -> "/images/italy-loafer.png";
                case "İpek Fular" -> "/images/italy-ipek-fular.png";
                case "Milano Moda" -> "/images/italy-milano-moda.png";
                case "Deri Atölyeleri" -> "/images/italy-deri-atolyeleri.png";
                case "Butikler" -> "/images/italy-butikler.png";
                case "Espresso Bar" -> "/images/italy-espresso-bar.png";
                case "Piazza Cafeleri" -> "/images/italy-piazza-cafeleri.png";
                case "Gelato Mekanları" -> "/images/italy-gelato-mekanlari.png";
                case "Aperitivo" -> "/images/italy-aperitivo.png";
                case "Canlı Sokaklar" -> "/images/italy-canli-sokaklar.png";
                case "Şarap Barları" -> "/images/italy-sarap-barlari.png";
                case "Amalfi" -> "/images/italy-amalfi.png";
                case "Göl Kasabaları" -> "/images/italy-gol-kasabalari.png";
                default -> null;
            };
        }

        if ("Japonya".equals(countryName)) {
            return switch (itemName) {
                case "Sushi" -> "/images/japan-sushi.png";
                case "Ramen" -> "/images/japan-ramen.png";
                case "Mochi" -> "/images/japan-mochi.png";
                case "Tokyo" -> "/images/japan-tokyo.png";
                case "Kyoto" -> "/images/japan-kyoto.png";
                case "Fuji Dağı" -> "/images/japan-mount-fuji.png";
                case "Minimal Evler" -> "/images/japan-minimal-homes.png";
                case "Tatami Odaları" -> "/images/japan-tatami-rooms.png";
                case "Akıllı Daireler" -> "/images/japan-smart-apartments.png";
                case "Minimal Kesimler" -> "/images/japan-minimal-cuts.png";
                case "Kimono Detayları" -> "/images/japan-kimono-details.png";
                case "Sneaker Stili" -> "/images/japan-sneaker-style.png";
                case "Teknoloji Mağazaları" -> "/images/japan-technology-stores.png";
                case "Tasarım Butikleri" -> "/images/japan-design-boutiques.png";
                case "Vintage Sokaklar" -> "/images/japan-vintage-streets.png";
                case "Çay Evleri" -> "/images/japan-tea-houses.png";
                case "Kedi Kafeleri" -> "/images/japan-cat-cafes.png";
                case "Minimal Cafeler" -> "/images/japan-minimal-cafes.png";
                case "Tokyo Işıkları" -> "/images/japan-tokyo-lights.png";
                case "Karaoke" -> "/images/japan-karaoke.png";
                case "İzakaya" -> "/images/japan-izakaya.png";
                case "Bambu Ormanları" -> "/images/japan-bamboo-forest.png";
                case "Kaplıcalar" -> "/images/japan-onsen.png";
                case "Sakura Parkları" -> "/images/japan-sakura-park.png";
                default -> null;
            };
        }

        if ("Hollanda".equals(countryName)) {
            return switch (itemName) {
                case "Stroopwafel" -> "/images/netherlands-stroopwafel.png";
                case "Peynir" -> "/images/netherlands-peynir.png";
                case "Poffertjes" -> "/images/netherlands-poffertjes.png";
                case "Amsterdam" -> "/images/netherlands-amsterdam.png";
                case "Kanallar" -> "/images/netherlands-kanallar.png";
                case "Van Gogh Müzesi" -> "/images/netherlands-van-gogh-muzesi.png";
                case "Kanal Evleri" -> "/images/netherlands-kanal-evleri.png";
                case "Minimal Daireler" -> "/images/netherlands-minimal-daireler.png";
                case "Açık Plan Evler" -> "/images/netherlands-acik-plan-evler.png";
                case "Rahat Sneaker" -> "/images/netherlands-rahat-sneaker.png";
                case "Yağmurluk" -> "/images/netherlands-yagmurluk.png";
                case "Minimal Çanta" -> "/images/netherlands-minimal-canta.png";
                case "Tasarım Mağazaları" -> "/images/netherlands-tasarim-magazalari.png";
                case "Çiçek Pazarları" -> "/images/netherlands-cicek-pazarlari.png";
                case "Vintage Dükkanlar" -> "/images/netherlands-vintage-dukkanlar.png";
                case "Kanal Kafeleri" -> "/images/netherlands-kanal-kafeleri.png";
                case "Bisiklet Dostu Mekanlar" -> "/images/netherlands-bisiklet-dostu-mekanlar.png";
                case "Fırınlar" -> "/images/netherlands-firinlar.png";
                case "Kanal Geceleri" -> "/images/netherlands-kanal-geceleri.png";
                case "Canlı Müzik" -> "/images/netherlands-canli-muzik.png";
                case "Kültür Barları" -> "/images/netherlands-kultur-barlari.png";
                case "Lale Bahçeleri" -> "/images/netherlands-lale-bahceleri.png";
                case "Sahil Kasabaları" -> "/images/netherlands-sahil-kasabalari.png";
                case "Yeşil Parklar" -> "/images/netherlands-yesil-parklar.png";
                default -> null;
            };
        }

        if ("İspanya".equals(countryName)) {
            return switch (itemName) {
                case "Paella" -> "/images/spain-paella.png";
                case "Tapas" -> "/images/spain-tapas.png";
                case "Churros" -> "/images/spain-churros.png";
                case "Barcelona" -> "/images/spain-barcelona.png";
                case "Madrid" -> "/images/spain-madrid.png";
                case "Sevilla" -> "/images/spain-sevilla.png";
                case "Akdeniz Evleri" -> "/images/spain-akdeniz-evleri.png";
                case "Renkli Seramikler" -> "/images/spain-renkli-seramikler.png";
                case "Modern Sahil Daireleri" -> "/images/spain-modern-sahil-daireleri.png";
                case "Keten Gömlek" -> "/images/spain-keten-gomlek.png";
                case "Renkli Elbise" -> "/images/spain-renkli-elbise.png";
                case "Rahat Sandalet" -> "/images/spain-rahat-sandalet.png";
                case "Yerel Pazarlar" -> "/images/spain-yerel-pazarlar.png";
                case "Tasarım Butikleri" -> "/images/spain-tasarim-butikleri.png";
                case "Moda Caddeleri" -> "/images/spain-moda-caddeleri.png";
                case "Tapas Cafeleri" -> "/images/spain-tapas.png";
                case "Sahil Kafeleri" -> "/images/spain-sahil-kafeleri.png";
                case "Meydan Mekanları" -> "/images/spain-meydan-mekanlari.png";
                case "Flamenko" -> "/images/spain-flamenko.png";
                case "Gece Meydanları" -> "/images/spain-gece-meydanlari.png";
                case "Rooftop Barlar" -> "/images/spain-rooftop-barlar.png";
                case "Costa Brava" -> "/images/spain-costa-brava.png";
                case "Adalar" -> "/images/spain-adalar.png";
                case "Zeytinlikler" -> "/images/spain-zeytinlikler.png";
                default -> null;
            };
        }

        if ("Avustralya".equals(countryName)) {
            return switch (itemName) {
                case "Avocado Toast" -> "/images/australia-avocado-toast.png";
                case "Barbekü" -> "/images/australia-barbecue.png";
                case "Lamington" -> "/images/australia-lamington.png";
                case "Sydney" -> "/images/australia-sydney.png";
                case "Melbourne" -> "/images/australia-melbourne.png";
                case "Great Barrier Reef", "Mercan Resifi" -> "/images/australia-great-barrier-reef.png";
                case "Sahil Evleri" -> "/images/australia-beach-houses.png";
                case "Modern Loft" -> "/images/australia-modern-loft.png";
                case "Açık Plan Evler" -> "/images/australia-open-plan-homes.png";
                case "Rahat Keten" -> "/images/australia-linen.png";
                case "Sandalet" -> "/images/australia-sandals.png";
                case "Surf Stili" -> "/images/australia-surf-style.png";
                case "Tasarım Pazarları" -> "/images/australia-design-markets.png";
                case "Yerel Markalar" -> "/images/australia-local-brands.png";
                case "Sahil Butikleri" -> "/images/australia-beach-boutiques.png";
                case "Brunch Cafeleri" -> "/images/australia-brunch-cafes.png";
                case "Sahil Cafeleri" -> "/images/australia-beach-cafes.png";
                case "Kahve Mekanları" -> "/images/australia-coffee-places.png";
                case "Rooftop Barlar" -> "/images/australia-rooftop-bars.png";
                case "Sahil Partileri" -> "/images/australia-beach-parties.png";
                case "Canlı Müzik" -> "/images/australia-live-music.png";
                case "Plajlar" -> "/images/australia-beaches.png";
                case "Ulusal Parklar" -> "/images/australia-national-parks.png";
                default -> null;
            };
        }

        if ("Türkiye".equals(countryName)) {
            return switch (itemName) {
                case "Kebap" -> "/images/turkey-kebab.png";
                case "Baklava" -> "/images/turkey-baklava.png";
                case "Türk Kahvesi" -> "/images/turkey-coffee.png";
                case "İstanbul" -> "/images/turkey-istanbul.png";
                case "Kapadokya" -> "holiday".equals(sectionId) ? "/images/turkey-cappadocia-landscape.png" : "/images/turkey-cappadocia.png";
                case "Pamukkale" -> "/images/turkey-pamukkale.png";
                case "Taş Evler" -> "/images/turkey-stone-houses.png";
                case "Bohem Sahil Evleri" -> "/images/turkey-aegean-beach-house.png";
                case "Modern Şehir Daireleri" -> "/images/turkey-modern-apartment.png";
                case "Rahat Şıklık" -> "/images/turkey-relaxed-chic.png";
                case "Akdeniz Tonları" -> "/images/turkey-mediterranean-fashion.png";
                case "Modern Şehir Stili" -> "/images/turkey-modern-city-style.png";
                case "Kapalıçarşı" -> "/images/turkey-grand-bazaar.png";
                case "Tasarım Butikleri" -> "/images/turkey-design-boutiques.png";
                case "Yerel Pazarlar" -> "/images/turkey-local-market.png";
                case "Boğaz Kafeleri" -> "/images/turkey-bosphorus-cafe.png";
                case "Kitapçı Kafeler" -> "/images/turkey-bookstore-cafe.png";
                case "Sahil Mekanları" -> "/images/turkey-seaside-restaurant.png";
                case "Meyhaneler" -> "/images/turkey-meyhane.png";
                case "Rooftoplar" -> "/images/turkey-rooftop.png";
                case "Canlı Müzik" -> "/images/turkey-live-music.png";
                case "Ege Koyları" -> "/images/turkey-aegean-bays.png";
                case "Yaylalar" -> "/images/turkey-highlands.png";
                default -> null;
            };
        }

        if ("Almanya".equals(countryName)) {
            return switch (itemName) {
                case "Pretzel" -> "/images/germany-pretzel.png";
                case "Schnitzel" -> "/images/germany-schnitzel.png";
                case "Strudel" -> "/images/germany-strudel.png";
                case "Berlin" -> "/images/germany-berlin.png";
                case "Münih" -> "/images/germany-munich.png";
                case "Köln" -> "/images/germany-cologne.png";
                case "Modern Daireler" -> "/images/germany-modern-apartments.png";
                case "Endüstriyel Loft" -> "/images/germany-industrial-loft.png";
                case "Bahçeli Evler" -> "/images/germany-garden-house.png";
                case "Minimal Ceket" -> "/images/germany-minimal-jacket.png";
                case "Sneaker" -> "/images/germany-sneaker.png";
                case "Pratik Çanta" -> "/images/germany-practical-bag.png";
                case "Tasarım Mağazaları" -> "/images/germany-design-stores.png";
                case "Yerel Pazarlar" -> "/images/germany-local-markets.png";
                case "Alışveriş Caddeleri" -> "/images/germany-shopping-streets.png";
                case "Üçüncü Nesil Kahve" -> "/images/germany-third-wave-coffee.png";
                case "Fırın Cafeleri" -> "/images/germany-bakery-cafes.png";
                case "Kitap Kafeler" -> "/images/germany-book-cafes.png";
                case "Berlin Kulüpleri" -> "/images/germany-berlin-clubs.png";
                case "Bira Bahçeleri" -> "/images/germany-beer-gardens.png";
                case "Konserler" -> "/images/germany-concerts.png";
                case "Bavyera" -> "/images/germany-bavaria.png";
                case "Orman Rotaları" -> "/images/germany-forest-routes.png";
                case "Göller" -> "/images/germany-lakes.png";
                default -> null;
            };
        }

        if ("Portekiz".equals(countryName)) {
            return switch (itemName) {
                case "Pastel de Nata" -> "/images/portugal-pastel-de-nata.png";
                case "Bacalhau" -> "/images/portugal-bacalhau.png";
                case "Deniz Ürünleri" -> "/images/portugal-seafood.png";
                case "Lizbon" -> "/images/portugal-lisbon.png";
                case "Porto" -> "/images/portugal-porto.png";
                case "Algarve" -> "holiday".equals(sectionId)
                        ? "/images/portugal-algarve-holiday.png"
                        : "/images/portugal-algarve.png";
                case "Azulejo Evler" -> "/images/portugal-azulejo-houses.png";
                case "Sahil Daireleri" -> "/images/portugal-coastal-apartments.png";
                case "Taş Evler" -> "/images/portugal-stone-houses.png";
                case "Keten Gömlek" -> "/images/portugal-linen-shirt.png";
                case "Hasır Çanta" -> "/images/portugal-straw-bag.png";
                case "Rahat Ayakkabı" -> "/images/portugal-comfort-shoes.png";
                case "Seramik Atölyeleri" -> "/images/portugal-ceramic-workshops.png";
                case "Yerel Butikler" -> "/images/portugal-local-boutiques.png";
                case "Tasarım Pazarları" -> "/images/portugal-design-markets.png";
                case "Pastane Cafeleri" -> "/images/portugal-pastry-cafes.png";
                case "Sahil Cafeleri" -> "/images/portugal-seaside-cafes.png";
                case "Teras Mekanları" -> "/images/portugal-terrace-venues.png";
                case "Fado Geceleri" -> "/images/portugal-fado-nights.png";
                case "Şarap Barları" -> "/images/portugal-wine-bars.png";
                case "Sahil Mekanları" -> "/images/portugal-seaside-venues.png";
                case "Douro Vadisi" -> "/images/portugal-douro-valley.png";
                case "Okyanus Rotaları" -> "/images/portugal-ocean-routes.png";
                default -> null;
            };
        }

        return null;
    }

    private static String fallbackImage(String countryName) {
        return switch (countryName) {
            case "Fransa" -> "/images/paris-luxury-night.png";
            case "ABD" -> "/images/usa-coastal-cities.png";
            case "İngiltere" -> "/images/uk-big-ben.png";
            case "BAE" -> "/images/uae-main.png";
            case "Japonya" -> "/images/japan-main.png";
            case "İtalya" -> "/images/italy-main.png";
            case "İspanya" -> "/images/spain-main.png";
            case "Hollanda" -> "/images/netherlands-main.png";
            case "İsviçre" -> "/images/switzerland-main.png";
            case "Yunanistan" -> "/images/greece-main.png";
            case "Kanada" -> "/images/canada-main.png";
            case "Güney Kore" -> "/images/korea-main.png";
            case "Avustralya" -> "/images/australia-sydney.png";
            case "Türkiye" -> "/images/turkey-main.png";
            case "Almanya" -> "/images/germany-main.png";
            case "Portekiz" -> "/images/portugal-main.png";
            default -> "/images/lifestyle-culture.png";
        };
    }

    private static String itemText(String countryName, String item, String title) {
        return item + ", " + countryName + " için " + title.toLowerCase() + " kategorisinde ülkenin karakterini yansıtan öne çıkan detaylardan biridir.";
    }

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        AuthUser currentUser = (AuthUser) session.getAttribute(SESSION_USER);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("recommendedCountry", recommendation(currentUser));
        model.addAttribute("favoriteCount", currentUser == null ? 0 : currentUser.favoriteCountries().size());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        Optional<AuthUser> user = userService.login(email, password);
        if (user.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "E-posta veya şifre hatalı.");
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/login";
        }

        session.setAttribute(SESSION_USER, user.get());
        String destination = (String) session.getAttribute(SESSION_AFTER_LOGIN);
        if (destination != null) {
            session.removeAttribute(SESSION_AFTER_LOGIN);
            return "redirect:" + destination;
        }
        return "redirect:/favorites";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        UserService.RegisterResult result = userService.resetPassword(email, password, confirmPassword);
        if (!result.success()) {
            redirectAttributes.addFlashAttribute("error", result.message());
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/forgot-password";
        }

        redirectAttributes.addFlashAttribute("success", "Şifren güncellendi. Yeni şifrenle giriş yapabilirsin.");
        redirectAttributes.addFlashAttribute("email", result.user().email());
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String style,
                           @RequestParam(required = false) String terms,
                           RedirectAttributes redirectAttributes) {
        UserService.RegisterResult result = userService.register(fullName, email, password, style, terms != null);
        if (!result.success()) {
            redirectAttributes.addFlashAttribute("error", result.message());
            redirectAttributes.addFlashAttribute("fullName", fullName);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("style", style);
            return "redirect:/register";
        }

        redirectAttributes.addFlashAttribute("success", "Hesabın oluşturuldu. Şimdi giriş yapabilirsin.");
        redirectAttributes.addFlashAttribute("email", result.user().email());
        return "redirect:/login";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String fullName,
                                @RequestParam String style,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        AuthUser currentUser = requireUser(session);
        Optional<AuthUser> updatedUser = userService.updateProfile(currentUser.email(), fullName, style);
        if (updatedUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("profileError", "Profil bilgileri güncellenemedi. Ad soyad ve yaşam tarzını kontrol et.");
            return "redirect:/favorites";
        }

        session.setAttribute(SESSION_USER, updatedUser.get());
        redirectAttributes.addFlashAttribute("profileSuccess", "Profil bilgilerin güncellendi.");
        return "redirect:/favorites";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/countries")
    public String countries(HttpSession session, Model model) {
        AuthUser currentUser = (AuthUser) session.getAttribute(SESSION_USER);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("favoriteSlugs", currentUser == null ? Set.of() : currentUser.favoriteCountries());
        return "countries";
    }

    @GetMapping("/lifestyles")
    public String lifestyles(HttpSession session, Model model) {
        AuthUser currentUser = (AuthUser) session.getAttribute(SESSION_USER);
        if (currentUser == null) {
            session.setAttribute(SESSION_AFTER_LOGIN, "/lifestyles");
            return "redirect:/login";
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("recommendedCountry", recommendation(currentUser));
        return "lifestyles";
    }

    @GetMapping("/discover")
    public String discover(HttpSession session, Model model) {
        AuthUser currentUser = (AuthUser) session.getAttribute(SESSION_USER);
        Country recommendedCountry = recommendation(currentUser);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("recommendedCountry", recommendedCountry);
        model.addAttribute("personalizedCountries", personalizedCountries(recommendedCountry));
        Country foodCountry = recommendedCountry == null ? COUNTRIES.get("spain") : recommendedCountry;
        GuideSection foodSection = guideSection(foodCountry, "foods");
        model.addAttribute("foodCountry", foodCountry);
        model.addAttribute("foodSection", foodSection);
        model.addAttribute("foodTitle", foodSection == null ? "" : String.join(", ",
                foodSection.items().stream().map(GalleryItem::title).toList()));
        return "discover";
    }

    @GetMapping("/favorites")
    public String favorites(HttpSession session, Model model) {
        AuthUser currentUser = (AuthUser) session.getAttribute(SESSION_USER);
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userInitials", initials(currentUser.fullName()));
        model.addAttribute("favoriteCountries", countriesBySlug(currentUser.favoriteCountries()));
        model.addAttribute("favoriteCount", currentUser.favoriteCountries().size());
        model.addAttribute("favoriteNotes", currentUser.favoriteNotes());
        model.addAttribute("favoriteCollections", currentUser.favoriteCollections());
        model.addAttribute("recommendedCountry", recommendation(currentUser));
        model.addAttribute("profileStyles", profileStyles());
        return "favorites";
    }

    @GetMapping("/country/{slug}")
    public String countryDetail(@PathVariable String slug, HttpSession session, Model model) {
        Country country = COUNTRIES.get(slug);
        if (country == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        model.addAttribute("country", country);
        AuthUser currentUser = (AuthUser) session.getAttribute(SESSION_USER);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isFavorite", currentUser != null && currentUser.favoriteCountries().contains(slug));
        model.addAttribute("relatedCountries", relatedCountries(country));
        model.addAttribute("matchReasons", matchReasons(country));
        model.addAttribute("practicalNotes", practicalNotes(country));
        model.addAttribute("scoreMetrics", scoreMetrics(country));
        model.addAttribute("citySuggestions", citySuggestions(country));
        model.addAttribute("dayPlan", dayPlan(country));
        return "country-detail";
    }

    @PostMapping("/api/favorites/{slug}")
    @ResponseBody
    public Map<String, Object> toggleFavorite(@PathVariable String slug, HttpSession session) {
        if (!COUNTRIES.containsKey(slug)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        AuthUser currentUser = requireUser(session);
        AuthUser updatedUser = userService.toggleFavorite(currentUser.email(), slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        session.setAttribute(SESSION_USER, updatedUser);
        boolean favorite = updatedUser.favoriteCountries().contains(slug);
        return Map.of("favorite", favorite, "count", updatedUser.favoriteCountries().size());
    }

    @PostMapping("/api/profile/recommendation/{slug}")
    @ResponseBody
    public Map<String, String> saveRecommendation(@PathVariable String slug, HttpSession session) {
        if (!COUNTRIES.containsKey(slug)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        AuthUser currentUser = requireUser(session);
        AuthUser updatedUser = userService.saveRecommendation(currentUser.email(), slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        session.setAttribute(SESSION_USER, updatedUser);
        return Map.of("recommendedCountry", updatedUser.recommendedCountry());
    }

    @PostMapping("/api/favorite-meta/{slug}")
    @ResponseBody
    public Map<String, String> updateFavoriteMeta(@PathVariable String slug,
                                                  @RequestParam(defaultValue = "") String note,
                                                  @RequestParam(defaultValue = "dream") String collection,
                                                  HttpSession session) {
        if (!COUNTRIES.containsKey(slug)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        AuthUser currentUser = requireUser(session);
        AuthUser updatedUser = userService.updateFavoriteMeta(currentUser.email(), slug, note, collection)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        session.setAttribute(SESSION_USER, updatedUser);
        return Map.of("status", "saved");
    }

    record Country(String slug, String name, String region, String lifestyle, String highlight, String currency,
                   String imageClass, String description, List<GuideSection> guideSections) {
    }

    record GuideSection(String id, String eyebrow, String title, String description, String image, String alt,
                        List<GalleryItem> items) {
    }

    record GalleryItem(String title, String description, String image) {
    }

    record ScoreMetric(String label, int value) {
    }

    record CitySuggestion(String name, String mood, String description) {
    }

    record DayPlan(String time, String title, String description) {
    }

    private static List<Country> countriesBySlug(Set<String> slugs) {
        return COUNTRY_ORDER.stream().filter(slugs::contains).map(COUNTRIES::get).toList();
    }

    private static Country recommendation(AuthUser user) {
        if (user == null) {
            return null;
        }
        return COUNTRIES.get(user.recommendedCountry());
    }

    private static List<Country> personalizedCountries(Country recommendedCountry) {
        if (recommendedCountry == null) {
            return countriesBySlug(Set.of("france", "uae", "switzerland"));
        }

        List<Country> countries = new ArrayList<>();
        countries.add(recommendedCountry);
        countries.addAll(relatedCountries(recommendedCountry).stream().limit(2).toList());
        return countries;
    }

    private static GuideSection guideSection(Country country, String sectionId) {
        if (country == null) {
            return null;
        }
        return country.guideSections().stream()
                .filter(section -> section.id().equals(sectionId))
                .findFirst()
                .orElse(null);
    }

    private static List<Country> relatedCountries(Country country) {
        return COUNTRY_ORDER.stream()
                .map(COUNTRIES::get)
                .filter(candidate -> !candidate.slug().equals(country.slug()))
                .filter(candidate -> candidate.region().equals(country.region())
                        || sharesKeyword(candidate.lifestyle(), country.lifestyle()))
                .limit(3)
                .toList();
    }

    private static boolean sharesKeyword(String firstLifestyle, String secondLifestyle) {
        for (String token : firstLifestyle.toLowerCase().split("[ &]+")) {
            if (token.length() > 3 && secondLifestyle.toLowerCase().contains(token)) {
                return true;
            }
        }
        return false;
    }

    private static List<String> matchReasons(Country country) {
        return List.of(
                country.lifestyle() + " odağı güçlü olduğu için yaşam ritmini net yansıtır.",
                country.highlight() + " çevresindeki şehir deneyimi premium keşif hissi verir.",
                country.region() + " atmosferiyle UrbanAura rotaları içinde dengeli bir seçenek sunar."
        );
    }

    private static List<String> practicalNotes(Country country) {
        return List.of(
                "Para birimi: " + country.currency(),
                "Öne çıkan şehir/rota: " + country.highlight(),
                "En uygun profil: " + country.lifestyle()
        );
    }

    private static List<ScoreMetric> scoreMetrics(Country country) {
        String lifestyle = country.lifestyle().toLowerCase();
        int luxury = lifestyle.contains("lüks") ? 94 : lifestyle.contains("kalite") ? 88 : 72;
        int culture = lifestyle.contains("kültür") || lifestyle.contains("tarih") || lifestyle.contains("gelenek") ? 95 : 78;
        int nature = lifestyle.contains("doğa") || lifestyle.contains("deniz") || lifestyle.contains("sahil")
                || lifestyle.contains("okyanus") ? 94 : 70;
        int modern = lifestyle.contains("modern") || lifestyle.contains("teknoloji") || lifestyle.contains("dinamik")
                || lifestyle.contains("kariyer") ? 93 : 76;
        int social = lifestyle.contains("sıcak") || lifestyle.contains("sosyal") || lifestyle.contains("enerji")
                || lifestyle.contains("lezzet") ? 91 : 74;

        return List.of(
                new ScoreMetric("Lüks", luxury),
                new ScoreMetric("Kültür", culture),
                new ScoreMetric("Doğa", nature),
                new ScoreMetric("Modernlik", modern),
                new ScoreMetric("Sosyal Hayat", social)
        );
    }

    private static List<CitySuggestion> citySuggestions(Country country) {
        return switch (country.slug()) {
            case "france" -> List.of(city("Paris", "Sanat & lüks", "Galeri, moda ve kafe kültürüyle güçlü bir ilk eşleşme."),
                    city("Nice", "Riviera sakinliği", "Deniz, güneş ve zarif sahil yaşamı isteyenlere uygun."),
                    city("Lyon", "Gastronomi", "Daha sakin ama kültürü kuvvetli bir şehir deneyimi sunar."));
            case "japan" -> List.of(city("Tokyo", "Modern enerji", "Teknoloji, tasarım ve gece ışıklarıyla güçlü şehir ritmi."),
                    city("Kyoto", "Gelenek", "Tapınaklar, çay kültürü ve sakin estetik için ideal."),
                    city("Osaka", "Sosyal lezzet", "Yemek, alışveriş ve canlı sokak atmosferi öne çıkar."));
            case "uae" -> List.of(city("Dubai", "Modern lüks", "Skyline, lounge ve alışveriş deneyimiyle güçlü seçenek."),
                    city("Abu Dhabi", "Sakin prestij", "Kültür, sahil ve daha rafine tempo isteyenlere yakın."),
                    city("Sharjah", "Kültür", "Sanat, müze ve daha sakin şehir yaşamı sunar."));
            case "italy" -> List.of(city("Roma", "Tarih & yaşam", "Klasik sokaklar, meydanlar ve güçlü kültür hissi."),
                    city("Floransa", "Sanat", "Müze, mimari ve estetik günlük yaşam için ideal."),
                    city("Milano", "Moda", "Tasarım, alışveriş ve modern İtalyan ritmi öne çıkar."));
            case "usa" -> List.of(city("New York", "Kariyer & enerji", "Hızlı tempo ve sınırsız şehir deneyimi."),
                    city("Los Angeles", "Sahil & yaratıcılık", "Açık hava, stil ve medya kültürünü birleştirir."),
                    city("San Francisco", "Teknoloji", "Yenilikçi atmosfer ve şehir-doğa dengesi sunar."));
            case "uk" -> List.of(city("Londra", "Prestij", "Kültür, moda ve iş hayatını güçlü biçimde birleştirir."),
                    city("Oxford", "Akademik tarih", "Klasik mimari ve sakin prestij hissi sunar."),
                    city("Edinburgh", "Karakter", "Tarihi sokaklar ve edebi atmosfer için ideal."));
            case "spain" -> List.of(city("Barcelona", "Sosyal Akdeniz", "Tasarım, sahil ve sıcak şehir ritmi öne çıkar."),
                    city("Madrid", "Kültür", "Müze, meydan ve canlı sosyal yaşam sunar."),
                    city("Sevilla", "Gelenek", "Flamenko, sıcak sokaklar ve tarihi doku için güçlü seçenek."));
            case "netherlands" -> List.of(city("Amsterdam", "Özgür şehir", "Kanallar, müzeler ve bisikletli yaşam ritmi."),
                    city("Rotterdam", "Modern tasarım", "Mimari, yaratıcılık ve dinamik şehir enerjisi."),
                    city("Utrecht", "Sakin merkez", "Kanal yaşamı ve daha huzurlu günlük tempo."));
            case "switzerland" -> List.of(city("Zürih", "Kalite", "Göl, düzen ve rafine şehir yaşamı sunar."),
                    city("Luzern", "Manzara", "Göl ve dağ atmosferini dengeli yaşatır."),
                    city("Cenevre", "Prestij", "Uluslararası yaşam ve sakin lüks için uygun."));
            case "greece" -> List.of(city("Atina", "Tarih", "Antik miras ve şehir enerjisini birleştirir."),
                    city("Santorini", "Romantik ada", "Gün batımı ve beyaz-mavi mimariyle güçlü his verir."),
                    city("Selanik", "Sosyal", "Kafe kültürü ve sıcak şehir yaşamı öne çıkar."));
            case "canada" -> List.of(city("Toronto", "Fırsat", "Kariyer, kültür ve güvenli metropol dengesi."),
                    city("Vancouver", "Doğa & şehir", "Okyanus, dağ ve modern yaşamı bir araya getirir."),
                    city("Montreal", "Kültür", "Avrupai atmosfer ve yaratıcı sosyal hayat sunar."));
            case "korea" -> List.of(city("Seul", "Enerji", "Teknoloji, K-kültür ve gece yaşamı çok güçlü."),
                    city("Busan", "Sahil", "Deniz, şehir ritmi ve daha rahat atmosfer sağlar."),
                    city("Jeju", "Doğa", "Ada yaşamı ve sakin kaçış için ideal."));
            case "australia" -> List.of(city("Sydney", "Sahil metropol", "Plaj, skyline ve açık hava yaşamı sunar."),
                    city("Melbourne", "Kültür", "Kafe, tasarım ve sanat ritmiyle öne çıkar."),
                    city("Brisbane", "Rahat tempo", "Sıcak iklim ve ferah günlük yaşam için uygun."));
            case "turkey" -> List.of(city("İstanbul", "Kültür", "Tarih, Boğaz ve şehir enerjisini birlikte sunar."),
                    city("İzmir", "Sahil & rahatlık", "Ege ritmi ve sosyal yaşam dengesi öne çıkar."),
                    city("Kapadokya", "Masalsı rota", "Doğa, tarih ve özgün konaklama deneyimi sunar."));
            case "germany" -> List.of(city("Berlin", "Yaratıcı modern", "Kültür, gece hayatı ve özgür şehir ritmi."),
                    city("Münih", "Düzen & kalite", "Kariyer, güven ve rafine şehir yaşamı sağlar."),
                    city("Hamburg", "Liman estetiği", "Su kenarı yaşamı ve sakin modernlik sunar."));
            case "portugal" -> List.of(city("Lizbon", "Okyanus & estetik", "Teraslar, tramvaylar ve sıcak şehir ritmi."),
                    city("Porto", "Karakter", "Tarihi doku, nehir ve gastronomi atmosferi."),
                    city("Algarve", "Sahil huzuru", "Deniz, güneş ve yavaş yaşam isteyenlere uygun."));
            default -> List.of(city(country.highlight(), "En güçlü rota", country.name() + " için başlangıç noktası olarak en dengeli seçenek."),
                    city(country.region(), "Bölge keşfi", "Benzer yaşam hissini çevre rotalarda genişletmek için uygun."),
                    city(country.lifestyle(), "Yaşam tarzı", "Tercihlerine yakın günlük ritmi keşfetmek için iyi bir tema."));
        };
    }

    private static CitySuggestion city(String name, String mood, String description) {
        return new CitySuggestion(name, mood, description);
    }

    private static List<DayPlan> dayPlan(Country country) {
        return switch (country.slug()) {
            case "france" -> List.of(
                    plan("Sabah", "Paris kafesinde yavaş başlangıç", "Kruvasan, kahve ve kısa bir Seine yürüyüşüyle zarif şehir ritmine gir."),
                    plan("Öğlen", "Louvre ve butik sokaklar", "Sanat molası sonrası Saint-Germain çevresinde tasarım mağazalarını keşfet."),
                    plan("Akşam", "Işıklar altında rooftop", "Şık bir restoran veya rooftop ile şehir ışıklarını premium bir kapanışa çevir."));
            case "japan" -> List.of(
                    plan("Sabah", "Minimal kahve ve sakin tapınak", "Tokyo ya da Kyoto'da sade tasarımlı bir kafe ve huzurlu bir bahçe rotası."),
                    plan("Öğlen", "Tasarım mağazaları ve ramen", "Şehir estetiğini, teknoloji mağazalarını ve yerel lezzetleri birlikte deneyimle."),
                    plan("Akşam", "Neon sokaklar ve izakaya", "Canlı sokak ışıkları, küçük tabaklar ve düzenli gece ritmiyle günü kapat."));
            case "uae" -> List.of(
                    plan("Sabah", "Marina kahvesi", "Skyline manzaralı bir lounge veya marina kafesinde güne başla."),
                    plan("Öğlen", "Alışveriş ve mimari keşif", "Dubai Mall, tasarım butikleri ve ikonik mimariyi tek rotada birleştir."),
                    plan("Akşam", "Çöl ya da rooftop", "Gün batımında çöl deneyimi veya şehir ışıklarına bakan şık bir teras seç."));
            default -> List.of(
                    plan("Sabah", country.highlight() + " çevresinde keşif", "Şehrin ana atmosferini hissettiren bir kafe ve kısa yürüyüşle başla."),
                    plan("Öğlen", country.lifestyle() + " odaklı rota", "Müze, pazar, sahil veya tasarım duraklarından sana en yakın olanı seç."),
                    plan("Akşam", "Yerel akşam deneyimi", country.name() + " karakterini yansıtan restoran, manzara veya sakin bir gece rotasıyla günü tamamla."));
        };
    }

    private static DayPlan plan(String time, String title, String description) {
        return new DayPlan(time, title, description);
    }

    private static List<String> profileStyles() {
        return List.of("Lüks & Konforlu", "Sanat & Kültür", "Doğa & Huzur", "Modern & Dinamik",
                "Geleneksel & Tarihi", "Sade & Minimal");
    }

    private static AuthUser requireUser(HttpSession session) {
        AuthUser currentUser = (AuthUser) session.getAttribute(SESSION_USER);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return currentUser;
    }

    private static String initials(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        String first = parts.length > 0 && !parts[0].isBlank() ? parts[0].substring(0, 1) : "U";
        String second = parts.length > 1 && !parts[1].isBlank() ? parts[1].substring(0, 1) : "A";
        return (first + second).toUpperCase();
    }
}
