package Actors

import akka.actor.ActorSystem

trait GeneralActors {
  implicit def system: ActorSystem
}

object ActorSystemContainer {
  lazy val system: ActorSystem = ActorSystem("GeneralActorSystem")
}
