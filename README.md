
![Fluxron Smart Kitchen](https://raw.githubusercontent.com/aerobless/FluxronSmartKitchen-Application/master/images/appscreen_header.jpg)

Die Firma Fluxron Solutions AG entwickelt in Amriswil Heizlösungen und Küchengeräte
auf Induktionsbasis. Diese Geräte besitzen eine Bluetooth-Schnittstelle, über welche
Einstellungen angepasst und ausführliche Laufzeit- und Fehlerprotokolle ausgelesen
werden können. Servicetechniker benötigen genau diese Informationen zur Reparatur
der Geräte in Grossküchen. Aufgrund der grossen Geräteanzahl, ist es schwierig die
Installationen im Überblick zu behalten.

In dieser Arbeit wurde eine Applikation für Android entwickelt, welche Techniker zur
Diagnose und Konfiguration nutzen. Die Lage der eingebauten Geräte wird auf Situationsfotos
markiert. Bei einem späteren Serviceeinsatz werden diese Positionen und der
Status aller Kochinstallationen abgerufen.

Zur Umsetzung des Projektes wurden agile Softwareentwicklungsmethoden eingesetzt.
Neben einer gründlichen Anforderungsanalyse wurde die Benutzeroberfläche mit Mockups
im Material Design konzipiert und mittels Usability-Walkthrough validiert.

Als Programmiersprache wurde Java 7 für Android eingesetzt. Die Anwendungsarchitektur
besteht aus drei Layern, welche mittels Messages über ein Event Bus System
kommunizieren. Lokal werden die Küchendaten in einer dokumentbasierten Datenbank
gespeichert. Die Kommunikation mit den Geräten erfolgt über das CANopen-Protokoll.
Zudem wurde die Architektur darauf ausgelegt, die Erweiterung um ein Cloud-Backend
einfach zu machen.

Der Funktionsumfang der Mobilapplikation umfasst die Verwaltung mehrerer Küchen
und der darin installierten Geräte. Küchen werden in einzelne Bereiche unterteilt und
der Status der Geräte wird regelmässig aktualisiert. Der Funktionsumfang wurde mit
einem erfolgreichen Praxistest vor Ort überprüft. Die Servicetechniker profitieren nun
von einer modernen Applikation, welche ihnen den Wartungsalltag erleichtert.

[Die Dokumentation dieser Arbeit kann hier gefunden werden.](https://github.com/aerobless/FluxronSmartKitchen-Documentation)

![Fluxron Smart Kitchen](https://raw.githubusercontent.com/aerobless/FluxronSmartKitchen-Application/master/images/app_footer.jpg)
