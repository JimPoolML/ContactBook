package appjpm4everyone.contactbook

import android.content.Context
import appjpm4everyone.contactbook.utils.Utils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UtilsTest{

    @Mock
    private var utils = Utils

    @Mock
    private lateinit var mockContext: Context

    private val emailChar: CharSequence = "example@test.com"
    private val myEmail: CharSequence = "jim.moreno.latorre@alumnos.upm.es"

    private val myName: String = "Jim Moreno"
    private val anotherName: String = "Pablo Neruda"
    private val anotherInitials: String = "PN"
    private val myInitials: String = "JM"

    private val spiderman: String = "Spiderman"
    private val sp: String = "Sp"

    private val numberFirst: CharSequence = "123456789"
    private val realNumber: CharSequence = "3214788862"
    private val lowNumber: CharSequence = "1234"
    private val highNumber: CharSequence = "123456789012345"


    @Before
    fun setUp(){
        //Mocking UserApi
        utils = Utils
        mockContext = mock(Context::class.java)

    }

    @Test
    fun getValidEmail() {
        Assert.assertTrue(utils.isValidEmail(emailChar))
        //It is longest
        Assert.assertFalse(utils.isValidEmail(myEmail))
    }

    @Test
    fun getValidPhoneNumber() {
        Assert.assertTrue(utils.isValidPhoneNumber(realNumber))
        Assert.assertTrue(utils.isValidPhoneNumber(numberFirst))
        Assert.assertFalse(utils.isValidPhoneNumber(lowNumber))
        Assert.assertFalse(utils.isValidPhoneNumber(highNumber))
        Assert.assertFalse(utils.isValidPhoneNumber(myEmail))
    }

    @Test
    fun getInitialChar() {
        Assert.assertEquals(utils.getInitialChar(myName), myInitials)
        Assert.assertEquals(utils.getInitialChar(anotherName), anotherInitials)
        Assert.assertEquals(utils.getInitialChar(spiderman), sp)
        //Check empty case
        Assert.assertEquals(utils.getInitialChar(""), "")
        Assert.assertNotEquals(utils.getInitialChar(myName), anotherInitials)
    }


    @Test
    fun getValidName() {
        //Check empty case
        Assert.assertFalse(utils.isValidName(""))
        Assert.assertTrue(utils.isValidName(myName))
        Assert.assertFalse(utils.isValidName(numberFirst))
    }

}