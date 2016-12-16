import com.atlassian.connect.playscala.AcConfig
import com.atlassian.connect.playscala.model.AcHostModelStore
import org.scalatest.{FunSuite, Matchers, PrivateMethodTester}
import play.api.libs.json.Json
import service.ViewerIssueValue
import play.api.mvc._

class ApplicationFunSuite extends FunSuite with Matchers with ViewerIssueValue{
  test("with positive num should return success") {
    validatePositiveField(1) shouldEqual "Correct number! "
  }
  
  test("with positive num should return fail") {
    validatePositiveField(-1) shouldEqual "Validation wasn't success. Please set positive number !"
  }
  override implicit def acConfig: AcConfig = ???
  override def acHostModelStore: AcHostModelStore = ???
}
