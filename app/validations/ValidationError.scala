package com.wunder.pets.validations

import java.util.regex.{Matcher, Pattern}

import org.postgresql.util.PSQLException

trait ValidationError {
  def message: String
}

final class IsEmpty(val field: String) extends ValidationError {
  override def message: String = s"$field cannot be empty"
}

final class NotGreaterThan[T](val field: String, val lowerBound: T) extends ValidationError {
  def message: String = s"$field must be greater than $lowerBound"
}

final class NotLessThan[T](val field: String, val upperBound: T) extends ValidationError {
  def message: String = s"$field must be less than $upperBound"
}

final class DuplicateValue(val e: PSQLException) extends ValidationError {
  override def message: String = {
    val regex = "Key \\((.*)\\)=\\((.*)\\) already exists."
    val m: Matcher = Pattern.compile(regex).matcher(e.getServerErrorMessage.getDetail);
    if (m.matches) {
      s"${m.group(1)} has a duplicate value of ${m.group(2)}"
    } else {
      "Could not determine field and value."
    }
  }
}

final class GeneralError(val message: String) extends ValidationError
