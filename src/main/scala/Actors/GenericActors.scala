package Actors

import akka.actor.SupervisorStrategy.{Resume, Stop}
import akka.actor.{Actor, ActorContext, ActorInitializationException, ActorRef, ActorSystem, OneForOneStrategy, Props}
import akka.pattern.ask
import akka.routing.RoundRobinPool
import akka.util.Timeout

import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.reflect.{ClassTag, classTag}

trait CoreActorSystem {
  implicit def system: ActorSystem
}

trait RootSupervisorHelper {
  self: CoreActorSystem =>
  lazy val rootSupervisor = system.actorOf(Props[RootSupervisor]/**/)
}
trait BaseActor extends Actor  {

  lazy val receivers = new ListBuffer[Actor.Receive]

  def receiver(pf: Actor.Receive): Unit = {
    receivers.append(pf)
  }

  final def receive = {
    case msg => {
      val matches = receivers.filter(pf => pf.isDefinedAt(msg))
      if(matches.size > 1) {
      }
      if(matches.nonEmpty)
      {
        matches.headOption.get(msg)
        interceptMsg(msg)
      }
      else {

      }
    }
  }

  def interceptMsg(msg:Any) = {
    //do nothing here
  }

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 5.second) {
    case ex:ActorInitializationException => {
      Stop
    }
    case ex: Exception =>
      Resume
  }

  def stopMe() {
    context.stop(self)
  }

  def tellSenderAndStop(message: AnyRef) {
    sender ! message
    stopMe()
  }
}
class RootSupervisor extends BaseActor {

  // Create a new actor under op supervisor
  receiver {
    case cmd:CreateActorRefCommand => {
      if(cmd.name.isDefined)
        sender ! context.actorOf(cmd.props, cmd.name.get)
      else sender ! context.actorOf(cmd.props)
    }
    // case props: Props => sender ! context.actorOf(props)
  }
}

trait GeneralActors {
  self: RootSupervisorHelper =>

  val availableProcessors = 1 //Runtime.getRuntime.availableProcessors

  def actorOf[T <:Actor : ClassTag](actorProperties: Props, name: Option[String] = None): ActorRef = {
    val finalName =  name.getOrElse(actorProperties.clazz.getSimpleName)
    if(finalName.toLowerCase().equals("none")) throw new IllegalArgumentException(
      """
        |
        |
        | Provide either name or type of actor for %-12s
        |
        |
      """.stripMargin.format(actorProperties))

    val actorRefFuture = ask(rootSupervisor, CreateActorRefCommand(actorProperties,Some(finalName)))(ActorHelper.actorCreationTimeout).mapTo[ActorRef]
    Await.result(actorRefFuture, ActorHelper.actorCreationTimeout.duration)
  }

  @deprecated("Name changed. Use actorOf instead","0.8.5.7-Kamon-snapshot")
  def getActorRef[T <:Actor : ClassTag](actorProperties: Props, name: Option[String] = None): ActorRef = {
    actorOf[T](actorProperties,name)
  }

  def actorOf[T <:Actor : ClassTag](clazz: Class[T]): ActorRef = {
    // val finalName = classTag[T].runtimeClass.getName
    val props = Props(clazz)
    val actorRefFuture = ask(rootSupervisor, CreateActorRefCommand(props,Some(props.clazz.getSimpleName)))(ActorHelper.actorCreationTimeout).mapTo[ActorRef]
    Await.result(actorRefFuture, ActorHelper.actorCreationTimeout.duration)
  }

  @deprecated("Named changed. Use actorOf instead","0.8.5.7-Kamon-snapshot")
  def getActorRef[T <:Actor : ClassTag](clazz: Class[T]): ActorRef = {
    actorOf[T](clazz)
  }

  def createRouters[T <:Actor : ClassTag](actorProperties: Props, nrOfInstances: Int = availableProcessors, name: Option[String] = None) = {
    actorOf[T](RoundRobinPool(nrOfInstances).props(actorProperties),name)
  }

  def createRoutersByType[T <:Actor : ClassTag](nrOfInstances: Int = availableProcessors) = {
    actorOf[T](RoundRobinPool(nrOfInstances).props(Props(classTag[T].runtimeClass)))
  }
}
case class CreateActorRefCommand(props: Props, name: Option[String] = None)

trait ActorHelper {

  /**
  Creates ActorRef using ActorSystem
    */
  def getActorRef[T](clazz: Class[T]): ActorRef = {
    ActorSystemContainer.system.actorOf(Props.create(clazz))
  }

  /**
  <p>If name is passed </p>
     then the below logic will try find the actorRef from its context
     else creates new actorRef
    */
  def getActorRef[TActor](context: ActorContext, clazz: Class[TActor],name:String = null): ActorRef = {

    if(name == null) return context.actorOf(Props.create(clazz))

    val child = context.child(name)
    if(child.isDefined) return child.get
    context.actorOf(Props.create(clazz),name)
  }

}

object ActorHelper {
  val actorCreationTimeout = Timeout(5.seconds)
  val askTimeout = Timeout(12.seconds)
  val commonOperationsTimeout = Timeout(12.seconds)
  val requestTimeout = Timeout(12.seconds)
}

object ActorSystemContainer  {
  lazy val system: ActorSystem =  ActorSystem("GeneralActorSystem")
}