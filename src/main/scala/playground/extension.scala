package playground

import schematic.Alt
import schematic.Field
import smithy4s.Hints

object CompletionSchematic {
  // from context
  type Result[+A] = List[String] => List[String]
}

class CompletionSchematic
  extends smithy4s.StubSchematic[CompletionSchematic.Result]
  with schematic.struct.GenericAritySchematic[CompletionSchematic.Result] {
  import CompletionSchematic.Result

  override def default[A]: Result[A] = _ => Nil

  override def genericStruct[S](
    fields: Vector[Field[Result, S, _]]
  )(
    const: Vector[Any] => S
  ): Result[S] = {
    println(fields.map(_.label))

    {
      case Nil       => fields.map(_.label).toList
      case h :: rest => fields.find(_.label == h).toList.flatMap(_.instance(rest))
    }
  }

  override def union[S](
    first: Alt[Result, S, _],
    rest: Vector[Alt[Result, S, _]],
  )(
    total: S => Alt.WithValue[Result, S, _]
  ): Result[S] = {
    val all = rest.prepended(first)

    {
      case head :: tail => all.find(_.label == head).toList.flatMap(_.instance(tail))

      case Nil => all.map(_.label).toList
    }

  }

  override def enumeration[A](
    to: A => (String, Int),
    fromName: Map[String, A],
    fromOrdinal: Map[Int, A],
  ): Result[A] = default

  override def bijection[A, B](f: Result[A], to: A => B, from: B => A): Result[B] = f

  override def withHints[A](fa: Result[A], hints: Hints): Result[A] = {
    println(hints)
    fa
  }

}

object extension {

  def complete = (??? : smithy4s.Schema[Unit]).compile(new CompletionSchematic).apply(Nil)

  def main(
    args: Array[String]
  ): Unit = println(complete)

}
