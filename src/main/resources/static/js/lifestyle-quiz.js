(function () {
    const root = document.querySelector("[data-quiz]");
    if (!root) {
        return;
    }

    const questions = [
        {
            title: "Hayalindeki günlük yaşamın ritmi nasıl?",
            options: [
                { label: "Sakin, huzurlu ve yavaş", scores: { switzerland: 3, portugal: 2, greece: 2, canada: 2 } },
                { label: "Sosyal, sıcak ve keyifli", scores: { italy: 3, spain: 3, turkey: 2, greece: 2 } },
                { label: "Hızlı, yenilikçi ve enerjik", scores: { usa: 3, korea: 3, germany: 2, japan: 2 } },
                { label: "Seçkin, konforlu ve zarif", scores: { france: 3, uae: 3, switzerland: 2, italy: 1 } }
            ]
        },
        {
            title: "Seni en çok hangi manzara kendine çeker?",
            options: [
                { label: "Dağlar, göller ve yeşil rotalar", scores: { switzerland: 3, canada: 3, germany: 1 } },
                { label: "Tarihi sokaklar ve sanat dolu şehirler", scores: { italy: 3, france: 3, uk: 2, turkey: 2 } },
                { label: "Işıklı skyline ve modern mimari", scores: { uae: 3, usa: 3, korea: 2 } },
                { label: "Sahil, adalar ve gün batımı", scores: { greece: 3, portugal: 3, australia: 3, spain: 2 } }
            ]
        },
        {
            title: "Bir hafta sonunu nasıl geçirmek istersin?",
            options: [
                { label: "Müze, galeri ve zarif kafeler", scores: { france: 3, italy: 2, uk: 2, japan: 1 } },
                { label: "Brunch, plaj ve açık hava", scores: { australia: 3, greece: 2, spain: 2, portugal: 2 } },
                { label: "Doğada yürüyüş ve sessiz bir akşam", scores: { canada: 3, switzerland: 3, portugal: 1 } },
                { label: "Alışveriş, rooftop ve gece hayatı", scores: { uae: 3, usa: 3, korea: 2, france: 1 } }
            ]
        },
        {
            title: "Yaşam alanında hangi stil sana daha yakın?",
            options: [
                { label: "Minimal, temiz ve düzenli", scores: { japan: 3, netherlands: 3, germany: 2, switzerland: 1 } },
                { label: "Tarihi, karakterli ve sıcak", scores: { italy: 3, uk: 2, turkey: 2, france: 2 } },
                { label: "Panoramik, modern ve prestijli", scores: { uae: 3, usa: 2, korea: 2 } },
                { label: "Denize ya da doğaya açılan ferah ev", scores: { australia: 3, portugal: 3, greece: 2, canada: 2 } }
            ]
        },
        {
            title: "Yeni bir ülkede senin için en önemli şey ne?",
            options: [
                { label: "Kariyer ve fırsatlar", scores: { usa: 3, germany: 3, korea: 2, uae: 2 } },
                { label: "Kültür, mutfak ve estetik", scores: { italy: 3, france: 3, turkey: 2, spain: 2 } },
                { label: "Güven, kalite ve sakinlik", scores: { switzerland: 3, canada: 3, netherlands: 2 } },
                { label: "Özgür, sosyal ve rahat yaşam", scores: { australia: 3, spain: 3, portugal: 2, greece: 2 } }
            ]
        },
        {
            title: "Hangi seyahat anı seni en iyi anlatır?",
            options: [
                { label: "Paris'te gece ışıkları ve bir sanat sergisi", scores: { france: 4, uk: 1 } },
                { label: "Tokyo'da sakin tasarım ve canlı sokaklar", scores: { japan: 4, korea: 2 } },
                { label: "Akdeniz'de uzun sofralar ve sahil akşamı", scores: { italy: 3, greece: 3, spain: 3, portugal: 2, turkey: 2 } },
                { label: "Modern bir metropolde lüks bir teras", scores: { uae: 4, usa: 3, germany: 1 } }
            ]
        }
    ];

    const countries = {
        france: { name: "Fransa", className: "france", description: "Sanat, zarafet ve rafine şehir yaşamı tercihlerinin en güçlü eşleşmesi Fransa.", href: "/country/france" },
        usa: { name: "ABD", className: "usa", description: "Hızlı ritim, kariyer olanakları ve modern şehir enerjisiyle ABD sana uyuyor.", href: "/country/usa" },
        uk: { name: "İngiltere", className: "uk", description: "Tarih, prestij ve kültür odaklı tercihlerinin karşılığı İngiltere.", href: "/country/uk" },
        uae: { name: "BAE", className: "uae", description: "Konfor, iddialı mimari ve premium metropol yaşamı için BAE öne çıkıyor.", href: "/country/uae" },
        japan: { name: "Japonya", className: "japan", description: "Düzen, minimal estetik ve yenilikçi yaşam zevkin Japonya ile eşleşiyor.", href: "/country/japan" },
        italy: { name: "İtalya", className: "italy", description: "Sanat, lezzet ve sıcak sosyal yaşamın zarif buluşması olarak İtalya sana yakın.", href: "/country/italy" },
        spain: { name: "İspanya", className: "spain", description: "Sosyal, sıcak ve hareketli Akdeniz ritmi seni İspanya'ya taşıyor.", href: "/country/spain" },
        netherlands: { name: "Hollanda", className: "netherlands", description: "Sade, modern ve özgür bir günlük yaşam için Hollanda güçlü bir seçim.", href: "/country/netherlands" },
        switzerland: { name: "İsviçre", className: "switzerland", description: "Doğayla iç içe sakinlik, kalite ve güven arayışın İsviçre'de karşılık buluyor.", href: "/country/switzerland" },
        greece: { name: "Yunanistan", className: "greece", description: "Ada hayatı, sakin gün batımları ve sıcak kültür için Yunanistan sana uygun.", href: "/country/greece" },
        canada: { name: "Kanada", className: "canada", description: "Ferah doğa, güven ve huzurlu şehir yaşamı beklentin Kanada ile uyumlu.", href: "/country/canada" },
        korea: { name: "Güney Kore", className: "korea", description: "Teknoloji, enerji ve çağdaş şehir kültürü tercihin Güney Kore'yi öne çıkarıyor.", href: "/country/korea" },
        australia: { name: "Avustralya", className: "australia", description: "Sahil, açık hava ve özgür modern yaşam ritmi için Avustralya güçlü eşleşmen.", href: "/country/australia" },
        turkey: { name: "Türkiye", className: "turkey", description: "Zengin tarih, sıcak kültür ve karakterli günlük yaşam tercihlerin Türkiye ile buluşuyor.", href: "/country/turkey" },
        germany: { name: "Almanya", className: "germany", description: "Düzenli hayat, kariyer ve modern kent kültürü için Almanya sana uygun.", href: "/country/germany" },
        portugal: { name: "Portekiz", className: "portugal", description: "Okyanus, sakin tempo ve estetik sokaklar arayışın Portekiz'le eşleşiyor.", href: "/country/portugal" }
    };

    const questionView = root.querySelector("[data-quiz-question]");
    const resultView = root.querySelector("[data-quiz-result]");
    const progress = root.querySelector("[data-quiz-progress]");
    const step = root.querySelector("[data-quiz-step]");
    const resultImage = root.querySelector("[data-result-image]");
    const resultTitle = root.querySelector("[data-result-title]");
    const resultDescription = root.querySelector("[data-result-description]");
    const resultProfile = root.querySelector("[data-result-profile]");
    const resultScore = root.querySelector("[data-result-score]");
    const resultPersonal = root.querySelector("[data-result-personal]");
    const resultReasons = root.querySelector("[data-result-reasons]");
    const resultAlternatives = root.querySelector("[data-result-alternatives]");
    const resultAltCards = root.querySelector("[data-result-alt-cards]");
    const resultCities = root.querySelector("[data-result-cities]");
    const resultLink = root.querySelector("[data-result-link]");
    const resultFavorite = root.querySelector("[data-result-favorite]");
    const restart = root.querySelector("[data-quiz-restart]");

    let currentQuestion = 0;
    let scores = {};
    let selectedAnswers = [];

    function renderQuestion() {
        const question = questions[currentQuestion];
        root.classList.remove("is-complete");
        root.classList.add("is-answering");
        step.textContent = "Soru " + (currentQuestion + 1) + " / " + questions.length;
        progress.style.width = ((currentQuestion + 1) / questions.length * 100) + "%";
        questionView.hidden = false;
        resultView.hidden = true;
        questionView.innerHTML =
            '<span class="question-count">0' + (currentQuestion + 1) + '</span>' +
            '<h2>' + question.title + '</h2>' +
            '<div class="quiz-options">' +
            question.options.map(function (option, index) {
                return '<button class="quiz-option" type="button" data-option="' + index + '">' +
                    '<span>' + option.label + '</span><b aria-hidden="true">+</b></button>';
            }).join("") +
            '</div>';
        questionView.classList.remove("question-enter");
        void questionView.offsetWidth;
        questionView.classList.add("question-enter");
    }

    function chooseOption(optionIndex) {
        const chosen = questions[currentQuestion].options[optionIndex];
        selectedAnswers.push(chosen.label);
        Object.keys(chosen.scores).forEach(function (slug) {
            scores[slug] = (scores[slug] || 0) + chosen.scores[slug];
        });

        currentQuestion += 1;
        if (currentQuestion < questions.length) {
            renderQuestion();
        } else {
            renderResult();
        }
    }

    function renderResult() {
        const ranked = Object.keys(countries).sort(function (first, second) {
            return (scores[second] || 0) - (scores[first] || 0);
        });
        const winnerSlug = ranked[0];
        const winner = countries[winnerSlug];
        const alternatives = ranked.slice(1, 3).map(function (slug) {
            return countries[slug].name;
        });
        const totalScore = Object.values(scores).reduce(function (sum, score) {
            return sum + score;
        }, 0);
        const winnerScore = scores[winnerSlug] || 0;
        const percentage = Math.max(72, Math.min(98, Math.round(winnerScore / Math.max(totalScore, 1) * 170)));

        root.classList.remove("is-answering");
        root.classList.add("is-complete");
        step.textContent = "Tamamlandı";
        progress.style.width = "100%";
        questionView.hidden = true;
        resultView.hidden = false;
        resultImage.className = "quiz-result-image " + winner.className;
        resultTitle.textContent = winner.name;
        if (resultProfile) {
            resultProfile.textContent = profileTitle(winnerSlug);
        }
        resultScore.textContent = "%" + percentage + " eşleşme";
        resultDescription.textContent = winner.description;
        if (resultPersonal) {
            resultPersonal.textContent = personalExplanation(winner.name);
        }
        resultReasons.innerHTML = [
            "Seçimlerin " + winner.name + " için güçlü bir yaşam ritmi sinyali verdi.",
            "Öne çıkan atmosferin bu rota ile daha dengeli ve premium görünüyor.",
            "Alternatifleri de inceleyerek kararını daha netleştirebilirsin."
        ].map(function (reason) {
            return "<li>" + reason + "</li>";
        }).join("");
        resultAlternatives.textContent = alternatives.join("  /  ");
        resultAltCards.innerHTML = ranked.slice(1, 3).map(function (slug) {
            const country = countries[slug];
            return '<a class="quiz-alt-card ' + country.className + '" href="' + country.href + '">' +
                '<span>' + country.name + '</span><small>Alternatif rota</small></a>';
        }).join("");
        if (resultCities) {
            resultCities.innerHTML = citySuggestions(winnerSlug).map(function (city) {
                return '<article><span>' + city.mood + '</span><strong>' + city.name + '</strong><small>' + city.note + '</small></article>';
            }).join("");
        }
        resultLink.href = winner.href;
        resultFavorite.dataset.favoriteToggle = winnerSlug;
        resultFavorite.classList.remove("is-favorite");
        resultFavorite.setAttribute("aria-pressed", "false");
        resultFavorite.querySelector("[data-favorite-icon]").textContent = "♡";
        resultFavorite.querySelector("[data-favorite-label]").textContent = "Favorilere Ekle";

        fetch("/api/profile/recommendation/" + winnerSlug, {
            method: "POST"
        }).catch(function () {
            // Recommendation display should remain usable if saving temporarily fails.
        });
    }

    function profileTitle(slug) {
        const profiles = {
            france: "Luxury Culture Seeker",
            italy: "Artful Social Explorer",
            uk: "Classic Heritage Lover",
            japan: "Minimal Design Mind",
            uae: "Modern Luxury Achiever",
            usa: "Big City Opportunity Seeker",
            korea: "Urban Energy Lover",
            germany: "Structured Modern Planner",
            netherlands: "Clean Freedom Seeker",
            switzerland: "Calm Quality Seeker",
            canada: "Nature Comfort Seeker",
            australia: "Open Air Lifestyle Lover",
            greece: "Slow Mediterranean Soul",
            portugal: "Ocean Calm Explorer",
            spain: "Warm Social Spirit",
            turkey: "Cultural Warmth Seeker"
        };
        return profiles[slug] || "Premium Travel Match";
    }

    function personalExplanation(countryName) {
        const highlights = selectedAnswers.slice(-3);
        if (!highlights.length) {
            return countryName + " sonucu, seçtiğin yaşam ritmi ve atmosfer tercihleriyle öne çıktı.";
        }
        return countryName + " çıktı çünkü özellikle \"" + highlights.join("\", \"") + "\" cevapların bu ülkenin yaşam tarzıyla güçlü biçimde eşleşti.";
    }

    function citySuggestions(slug) {
        const cities = {
            france: [
                { name: "Paris", mood: "Sanat & lüks", note: "Moda, müze ve kafe ritmi" },
                { name: "Nice", mood: "Riviera", note: "Deniz ve zarif sahil yaşamı" },
                { name: "Lyon", mood: "Gastronomi", note: "Daha sakin kültür rotası" }
            ],
            japan: [
                { name: "Tokyo", mood: "Modern", note: "Teknoloji ve gece ışıkları" },
                { name: "Kyoto", mood: "Gelenek", note: "Tapınaklar ve sakin estetik" },
                { name: "Osaka", mood: "Sosyal", note: "Lezzet ve şehir enerjisi" }
            ],
            uae: [
                { name: "Dubai", mood: "Luxury", note: "Skyline ve premium yaşam" },
                { name: "Abu Dhabi", mood: "Prestij", note: "Kültür ve daha sakin tempo" },
                { name: "Sharjah", mood: "Kültür", note: "Müze ve sanat odağı" }
            ],
            italy: [
                { name: "Roma", mood: "Tarih", note: "Klasik sokaklar ve meydanlar" },
                { name: "Floransa", mood: "Sanat", note: "Mimari ve galeri atmosferi" },
                { name: "Milano", mood: "Moda", note: "Tasarım ve modern ritim" }
            ],
            usa: [
                { name: "New York", mood: "Enerji", note: "Kariyer ve büyük şehir" },
                { name: "Los Angeles", mood: "Yaratıcı", note: "Sahil ve stil kültürü" },
                { name: "San Francisco", mood: "Tech", note: "Yenilikçi yaşam dengesi" }
            ],
            uk: [
                { name: "Londra", mood: "Prestij", note: "Kültür, moda ve iş hayatı" },
                { name: "Oxford", mood: "Tarih", note: "Akademik ve klasik atmosfer" },
                { name: "Edinburgh", mood: "Karakter", note: "Edebi ve tarihi sokaklar" }
            ],
            spain: [
                { name: "Barcelona", mood: "Sosyal", note: "Tasarım, sahil ve sıcak ritim" },
                { name: "Madrid", mood: "Kültür", note: "Müze ve canlı meydanlar" },
                { name: "Sevilla", mood: "Gelenek", note: "Flamenko ve tarihi doku" }
            ],
            netherlands: [
                { name: "Amsterdam", mood: "Özgür", note: "Kanallar ve müze kültürü" },
                { name: "Rotterdam", mood: "Modern", note: "Mimari ve yaratıcılık" },
                { name: "Utrecht", mood: "Sakin", note: "Kanal yaşamı ve dingin tempo" }
            ],
            switzerland: [
                { name: "Zürih", mood: "Kalite", note: "Göl, düzen ve rafine yaşam" },
                { name: "Luzern", mood: "Manzara", note: "Göl ve dağ atmosferi" },
                { name: "Cenevre", mood: "Prestij", note: "Uluslararası sakin lüks" }
            ],
            greece: [
                { name: "Atina", mood: "Tarih", note: "Antik miras ve şehir enerjisi" },
                { name: "Santorini", mood: "Romantik", note: "Gün batımı ve ada yaşamı" },
                { name: "Selanik", mood: "Sosyal", note: "Kafe kültürü ve sıcaklık" }
            ],
            canada: [
                { name: "Toronto", mood: "Fırsat", note: "Kariyer ve güvenli metropol" },
                { name: "Vancouver", mood: "Doğa", note: "Okyanus, dağ ve modern yaşam" },
                { name: "Montreal", mood: "Kültür", note: "Avrupai ve yaratıcı atmosfer" }
            ],
            korea: [
                { name: "Seul", mood: "Enerji", note: "Teknoloji ve K-kültür" },
                { name: "Busan", mood: "Sahil", note: "Deniz ve rahat şehir ritmi" },
                { name: "Jeju", mood: "Doğa", note: "Ada kaçışı ve sakinlik" }
            ],
            australia: [
                { name: "Sydney", mood: "Sahil", note: "Plaj, skyline ve açık hava" },
                { name: "Melbourne", mood: "Kültür", note: "Kafe, tasarım ve sanat" },
                { name: "Brisbane", mood: "Rahat", note: "Sıcak iklim ve ferah tempo" }
            ],
            turkey: [
                { name: "İstanbul", mood: "Kültür", note: "Boğaz, tarih ve şehir enerjisi" },
                { name: "İzmir", mood: "Sahil", note: "Ege ritmi ve sosyal yaşam" },
                { name: "Kapadokya", mood: "Masalsı", note: "Doğa ve özgün konaklama" }
            ],
            germany: [
                { name: "Berlin", mood: "Yaratıcı", note: "Kültür ve özgür şehir ritmi" },
                { name: "Münih", mood: "Düzen", note: "Kariyer, kalite ve güven" },
                { name: "Hamburg", mood: "Liman", note: "Su kenarı modernliği" }
            ],
            portugal: [
                { name: "Lizbon", mood: "Okyanus", note: "Teraslar ve sıcak şehir ritmi" },
                { name: "Porto", mood: "Karakter", note: "Nehir, tarih ve gastronomi" },
                { name: "Algarve", mood: "Huzur", note: "Deniz, güneş ve yavaş yaşam" }
            ]
        };
        return cities[slug] || [
            { name: countries[slug].name, mood: "Ana rota", note: "Ülke atmosferine güçlü giriş" },
            { name: "Merkez şehir", mood: "Keşif", note: "Günlük yaşamı gözlemle" },
            { name: "Sakin bölge", mood: "Denge", note: "Alternatif yaşam ritmini hisset" }
        ];
    }

    questionView.addEventListener("click", function (event) {
        const button = event.target.closest("[data-option]");
        if (button) {
            chooseOption(Number(button.dataset.option));
        }
    });

    restart.addEventListener("click", function () {
        scores = {};
        selectedAnswers = [];
        currentQuestion = 0;
        renderQuestion();
    });

    renderQuestion();
}());
