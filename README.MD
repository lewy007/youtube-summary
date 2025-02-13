<h1 align="center">YOUTUBE SUMMARY</h1>
Aplikacja na podstawie filmu z youtube (a dokładnie napisów generowanych automatycznie) potrafi nam go streścić. 
Możemy w ogóle nie oglądać filmu i mieć z niego notatkę. 
Wchodzimy na endpoint, podajemy id filmu i dostajemy streszczenie w formacie pdf.


Napisy z youtube można pobierać poprze API (jest to uciążliwe) lub przez scrappera jak np. [yt-dlp] (https://github.com/yt-dlp/yt-dlp)

## Jak zbudować aplikację samodzielnie

#### Lokalnie:
<ol>
<li>Bibliotekę można pobrać lokalnie i wskazać w zmiennych środowiskowych jej lokalizację.</li>

<li>Pobranie repozytorium:</li>

```
https://github.com/yt-dlp/yt-dlp.git
```

<li>Uruchomienie biblioteki, która pobiera automatycznie generowane napisy w języku polskim oraz pomija pobieranie całego filmu, ponieważ chcemy tylko napisy.</li>

```
yt-dlp --write-auto-sub --sub-lang pl --skip-download https://youtu.be/YIMvxQaYjGQ
```
</ol>

#### Docker:
<ol>
<li>Obraz znajduje się pod tym linkiem -> (https://hub.docker.com/r/jauderho/yt-dlp/tags).</li>

<li>Pobranie obrazu:</li>

```
docker run --rm -it jauderho/yt-dlp:latest
```

<li>Lub jednoczesne pobranie i uruchomienie. Musimy zamontować volumen, bo inaczej napisy zostaną pobrane wewnętrznie do kontenera, do którego jest trochę utrudniony dostęp:</li>

```
docker run --rm -v "C:\Users\Peter:/downloads" jauderho/yt-dlp:latest --write-auto-sub --sub-lang pl --skip-download https://youtu.be/YIMvxQaYjGQ
```

</ol>

#### Docker + Java:
<ol>
<li>Wczytanie pliku z napisami i streszczenie przy wykorzystaniu modelu Ollama wersja llama 3.2.</li>
<li>https://ollama.com/download/windows -> ściągamy na windowsa i instalujemy, dodatkowo dodajemy zmienną środowiskową ollama.</li>

<li>Uruchamiamy lokalnie lekką wersję ollamy (2GB) podając komendę w terminalu:</li>

```
ollama run llama3.2
```

<li>Możemy wykorzystać wersję OpenAI i chat GPT, ale to wersja płatna. Najpierw trzeba doładować konto na OpenAI:</li>
<li>Po uruchomieniu aplikacji wpisujmey poniższy adres URL. 
Aplikacja pobierze napisy w celu streszczenia filmu oraz doda zadania do filmu (Ollama),
a następnie zwróci informacje w formacie pdf:</li>

```
localhost:8080/get-subtitles?videoId=qHcPKdZZKfY
```

</ol>