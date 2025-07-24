package api

import io.circe.generic.auto._

case class LoanRequest(userId: String, bookId: String)
case class ReturnRequest(userId: String, bookId: String)
case class SearchRequest(title: String)
case class RecommendationRequest(userId: String)
case class ApiResponse[T](success: Boolean, data: Option[T] = None, message: Option[String] = None)
