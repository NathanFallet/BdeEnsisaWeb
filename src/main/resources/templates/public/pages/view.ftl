<#import "../../template.ftl" as template>
<@template.page>
    <div class="container-fluid py-4">
        <div class="card">
            <div class="card-body">
                <h1 class="mb-4">${title}</h1>
                ${markdown}
            </div>
        </div>
    </div>
</@template.page>
