package com.galaxy.galaxynet.model

enum class TaskCompletionState(val state: String) {
    NEW("جديده"),
    IN_PROGRESS("قيد التنفيذ"),
    COMPLETED("مكتمله")

}

enum class TasksCategory(val category: String){
    PROGRAMMING("برمجه"),
    INSTALLATION("تركيب"),
    PROGRAMMING_AND_INSTALLATION("برمجه و تركيب"),
    MAINTENANCE("صيانه"),
    PROBLEM_SOLVING("حل مشكله"),
    DISTRIBUTION("توزيع"),
    PLANNING("تخطيط"),
    OTHER("اخري")
}

enum class TaskAcceptanceStatus(val state: String) {
    PENDING("معلقه"),
    ACCEPTED("مقبوله"),
    REJECTED("مرفوضه")
}