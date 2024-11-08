# MP06-UF1 Fites #

## Arrencada ràpida ##
Execució ràpida dels diferents exemples i resolusions de problemes

## Windows ##
```bash
.\run.ps1 cat.iesesteveterradas.fites.FitesMain
```

```bash
.\run.ps1 cat.iesesteveterradas.fites.Exercici0
.\run.ps1 cat.iesesteveterradas.fites.Exercici1
.\run.ps1 cat.iesesteveterradas.fites.Exercici2Escriu
.\run.ps1 cat.iesesteveterradas.fites.Exercici2Llegeix
.\run.ps1 cat.iesesteveterradas.fites.Exercici3
.\run.ps1 cat.iesesteveterradas.fites.Exercici4
```

## Linux ##
```bash
run.sh  cat.iesesteveterradas.fites.FitesMain
```

```bash
run.sh  cat.iesesteveterradas.fites.Exercici0
run.sh  cat.iesesteveterradas.fites.Exercici1
run.sh  cat.iesesteveterradas.fites.Exercici2Escriu
run.sh  cat.iesesteveterradas.fites.Exercici2Llegeix
run.sh  cat.iesesteveterradas.fites.Exercici3
run.sh  cat.iesesteveterradas.fites.Exercici4
```

## Compilació i funcionament ##

### Execució senzilla ###

#### Windows ####
```bash
.\run.ps1 <com.project.Main> <param1> <param2> <param3>
run.sh <com.project.Main> <param1> <param2> <param3>
```
#### Linux ####
```bash
.\run.ps1 <com.project.Main> <param1> <param2> <param3>
run.sh <com.project.Main> <param1> <param2> <param3>
```

On:
* <com.project.Main>: és la classe principal que vols executar.
* \<param1>, \<param2>, \<param3>: són els paràmetres que necessites passar a la teva aplicació.


### Execució pas a pas ###

Si prefereixes executar el projecte pas a pas, pots seguir les següents instruccions:

Neteja el projecte per eliminar fitxers anteriors:
```bash
mvn clean
```

Compila el projecte:
```bash
mvn compile test
```

Executa la classe principal:
```bash
mvn exec:java -q -Dexec.mainClass="<com.project.Main>" <param1> <param2> <param3>
```

On:
* <com.project.Main>: és la classe principal que vols executar.
* \<param1>, \<param2>, \<param3>: són els paràmetres que necessites passar a la teva aplicació.
