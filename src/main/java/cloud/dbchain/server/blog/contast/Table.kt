package cloud.dbchain.server.blog.contast

object Common {
    const val id = "id"
    const val created_by = "created_by"
    const val created_at = "created_at"
    const val tx_hash = "tx_hash"
}

object Blogs {
    const val tableName = "blogs"
    const val title = "title"
    const val body = "body"
    const val img = "img"
}

object User {
    const val tableName = "user"
    const val name = "name"
    const val age = "age"
    const val dbchain_key = "dbchain_key"
    const val sex = "sex"
    const val status = "status"
    const val photo = "photo"
    const val motto = "motto"
}

object Discuss {
    const val tableName = "discuss"
    const val blog_id = "blog_id"
    const val discuss_id = "discuss_id"
    const val text = "text"
}