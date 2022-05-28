import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toKString
import libpq.*
import platform.posix.exit
import dto.User
import com.entity.getColumnNames
import com.entity.setVal
import dto.UserDto

const val Host = "localhost"
const val Port = "5432"
const val Usr = "postgres"
const val Password = "root"
const val DataBase = "postgres"

class DB {
    private var conn: CPointer<PGconn>
    init {
        val connStr = "host=$Host port=$Port user=$Usr password=$Password dbname=$DataBase"
        println("Connection String is - $connStr")
        conn = PQconnectdb(connStr)!!
        if(PQstatus(conn) != ConnStatusType.CONNECTION_OK){
            println("Connection to database failed ${PQstatus(conn)} ")
            do_exit(conn)
        }else{
            println("Connected to $Host:$Port/$DataBase with $Usr")
        }
    }

    private fun do_exit(conn: CPointer<PGconn>) {
        PQfinish(conn)
        exit(1)
    }

    fun addUser(usr: UserDto): String?{
        val insert = """insert into users (username, firstname, lastname)
                values('${usr.username}', '${usr.firstname}', '${usr.lastname}') 
                
                RETURNING id;"""
        println("inserting ... $insert")
        val res = PQexec(conn, insert)
        if (PQresultStatus(res) != PGRES_TUPLES_OK) {
            println("Insert failed for $usr with status ${PQresultStatus(res)}")
            PQclear(res)
            return "-1"
        }
        return PQgetvalue(res, 0, 0)?.toKString()
    }

    fun getUsers(): MutableList<User>{
        val users = mutableListOf<User>()
        val res = PQexec(conn, "SELECT * FROM users LIMIT 5")
        if (PQresultStatus(res) != PGRES_TUPLES_OK) {
            println("No data retrieved")
            PQclear(res)
        }else{
            val rows = PQntuples(res)
            for(i in 0 until rows){
                val usr = User()
                val colls = usr.getColumnNames()
                colls.forEach {
                    val colNum = PQfnumber(res, it);
                    val colVal = PQgetvalue(res, i, colNum)?.toKString()
                    usr.setVal(it, colVal)
                }
                users.add(usr)
            }
            PQclear(res)
        }
        println("Users are \n $users")
        return users
    }
}
