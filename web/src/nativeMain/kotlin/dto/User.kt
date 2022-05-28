package dto

import com.annotation.Column
import com.annotation.Entity
import kotlinx.serialization.*

@Entity(table = "users")
@Serializable
class User(
    @Column(name = "id") var id: String? = null,
    @Column(name = "username") var username: String? = null,
    @Column(name = "firstname")  var firstname: String? = null,
    @Column(name = "lastname")  var lastname: String? = null,
){
    override fun toString(): String{
        return "{id: $id, username: $username, firstname: $firstname, lastname: $lastname}"
    }
}

@Serializable
data class UserDto(
    val username: String,
    val firstname: String,
    val lastname: String,
)




