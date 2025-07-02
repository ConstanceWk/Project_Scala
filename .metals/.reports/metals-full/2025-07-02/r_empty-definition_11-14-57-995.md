error id: file://<WORKSPACE>/project-root/src/main/scala/gui/LibraryGUI.scala:`<none>`.
file://<WORKSPACE>/project-root/src/main/scala/gui/LibraryGUI.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -scalafx/scene/control/button.
	 -scalafx/scene/control/button#
	 -scalafx/scene/control/button().
	 -scalafx/scene/layout/button.
	 -scalafx/scene/layout/button#
	 -scalafx/scene/layout/button().
	 -button.
	 -button#
	 -button().
	 -scala/Predef.button.
	 -scala/Predef.button#
	 -scala/Predef.button().
offset: 749
uri: file://<WORKSPACE>/project-root/src/main/scala/gui/LibraryGUI.scala
text:
```scala
package gui

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout._
import services.LibraryCatalog
import utils.JsonIO
import services.given_Encoder_LibraryCatalog
import services.given_Decoder_LibraryCatalog

object LibraryGUI extends JFXApp3 {

  override def start(): Unit = {
    val jsonPath = "Data/Library.json"
    val loadedCatalog = JsonIO.loadFromFile[LibraryCatalog](jsonPath).getOrElse(LibraryCatalog(Nil, Nil, Nil))
    val catalog = loadedCatalog.synchronizeBookAvailability

    stage = new JFXApp3.PrimaryStage {
      title = "📚 Bibliothèque"
      scene = new Scene(600, 400) {
        val label = new Label("Bienvenue dans la bibliothèque !")
        val butto@@n = new Button("Voir les livres disponibles")
        val resultArea = new TextArea {
          editable = false
        }

        button.onAction = _ => {
          val available = catalog.availableBooks
            .map(b => s"${b.title} (${b.authors.mkString(", ")})")
            .mkString("\n")
          resultArea.text = available
        }

        root = new VBox(10, label, button, resultArea)
      }
    }
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.