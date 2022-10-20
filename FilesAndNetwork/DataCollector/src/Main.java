import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        HtmlParser.metroHTMLParser();
        FileFinder.startSearch("C:\\skillbox\\java_basics\\FilesAndNetwork\\DataCollector\\data");
        // FileFinder.startSearch("E:\\Repozitoriy\\dpo_java_basics\\FilesAndNetwork\\DataCollector\\data1\\code.html");
        System.out.println(DataClass.mapLineNumberToStations);
        JsonUtils.CreateJsonFile();
    }
}

/*

Задание 1. Программа, собирающая данные из разных источников
Что нужно сделать

Выполните задание в отдельном проекте, создайте для проекта директорию FilesAndNetwork/DataCollector.

Напишите программу, которая:

Получает HTML-код страницы «Список станций Московского метрополитена» с помощью библиотеки jsoup.
Парсит полученную страницу и получает из неё:
линии московского метро (имя и номер линии, цвет не нужен);
станции московского метро (имя станции и номер линии).
Собирает данные о станциях метро — даты их открытия и глубину станций — из файлов формата CSV и JSON, обходя папки, лежащие в архиве.
Разархивируйте архив и напишите код, который будет обходить все вложенные папки, искать в папках файлы JSON и CSV и обрабатывать их в зависимости от формата.
Метод для обхода папок должен принимать путь до папки в которой надо производить поиск.
Создаёт и записывает на диск два JSON-файла:
Файл со списком станций по линиям и списком линий по формату JSON-файла из проекта SPBMetro (файл map.json)
Файл stations.json со свойствами станций в следующем формате:
Пример:

Если каких-то свойств для той или иной станции нет, то в файле не должно быть соответствующих ключей.
Читает файл map.json и выводит в консоль количество станций на каждой линии.


Советы и рекомендации
Все варианты подключения библиотеки jsoup в проект — на странице скачивания библиотеки.
Для подбора и проверки селекторов используйте онлайн-сервис jsoup.
Прочитайте статью «Что такое JSON».
При изучении кода страницы удобно использовать консоль разработчика в браузере. Для этого нажмите F12,
перейдите во вкладку Elements и найдите тег <div id="metrodata">. В нём содержатся таблицы с линиями, станциями и пересадками.
Обращайте внимание на классы, напишите селекторы на основе найденных классов. Посмотрите в документации jsoup, как получать элементы по селекторам


Критерии оценки
Принято — программа собирает данные из всех источников, создаёт и записывает на диск два JSON-файла в соответствии с форматом,
показанным выше, а также читает файл map.json и выводит в консоль количество станций на каждой линии.

На доработку — задание не выполнено.

 */