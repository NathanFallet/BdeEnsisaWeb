<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="row">
        <div class="col-md-8">
            <div class="card card-body mt-4">
                <h6 class="mb-0">Profil de l'utilisateur</h6>
                <p class="text-sm mb-0">Visualiser les informations d'un utilisateur du site</p>
                <hr class="horizontal dark my-3">

                <form method="post">
                    <div class="row">
                        <div class="col-6">
                            <label for="first_name" class="form-label">Prénom</label>
                            <input type="text" class="form-control" name="first_name" id="first_name" value="${user.firstName}">
                        </div>
                        <div class="col-6">
                            <label for="last_name" class="form-label">Nom</label>
                            <input type="text" class="form-control" name="last_name" id="last_name" value="${user.lastName}">
                        </div>
                    </div>

                    <label for="email" class="form-label mt-4">Email</label>
                    <input type="text" class="form-control" name="email" id="email" value="${user.email}">

                    <div class="row">
                        <div class="col-4">
                            <label for="year" class="form-label mt-4">Année</label>
                            <select name="year" class="form-control" id="year">
                                <option value="1A" <#if user.year == "1A">selected</#if>>1A</option>
                                <option value="2A" <#if user.year == "2A">selected</#if>>2A</option>
                                <option value="3A" <#if user.year == "3A">selected</#if>>3A</option>
                                <option value="other" <#if user.year == "other">selected</#if>>4A et plus</option>
                            </select>
                        </div>
                        <div class="col-8">
                            <label for="option" class="form-label mt-4">Option</label>
                            <select name="option" class="form-control" id="option">
                                <option value="ir" <#if user.option == "ir">selected</#if>>Informatique et Réseaux</option>
                                <option value="ase" <#if user.option == "ase">selected</#if>>Automatique et Systèmes embarqués</option>
                                <option value="meca" <#if user.option == "meca">selected</#if>>Mécanique</option>
                                <option value="tf" <#if user.option == "tf">selected</#if>>Textile et Fibres</option>
                                <option value="gi" <#if user.option == "gi">selected</#if>>Génie Industriel</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="d-flex justify-content-end mt-4">
                        <a class="btn btn-light m-0" href="/admin/users">Annuler</a>
                        <input type="submit" class="btn bg-gradient-primary m-0 ms-2" value="Modifier">
                    </div>
                </form>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card card-body mt-4">
                <h6 class="mb-0">Statut de l'utilisateur</h6>
                <hr class="horizontal dark my-3">

                <#if user.cotisant??>
                <div class="alert alert-success">
                    Cet utilisateur est cotisant jusqu'au ${user.cotisant.formatted}.
                </div>
                <#else>
                <div class="alert alert-danger text-white">
                    Cet utilisateur n'est pas cotisant.
                </div>
                </#if>

                <hr class="horizontal dark my-3">

                <form method="post">
                    <label for="expiration" class="form-label">Définir le statut de cotisant jusqu'au :</label>
                    <input class="form-control datetimepicker" type="text" placeholder="Date d'expiration" name="expiration" id="expiration" data-input>

                    <div class="d-flex justify-content-end mt-4">
                        <button class="btn btn-light m-0" type="button" onclick="oneYear();">1 an</button>
                        <button class="btn btn-light m-0 ms-2" type="button" onclick="threeYears();">3 ans</button>
                        <input type="submit" class="btn bg-gradient-primary m-0 ms-2" value="Définir">
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<script src="/js/plugins/flatpickr.min.js"></script>
<script>
    if (document.querySelector('.datetimepicker')) {
      flatpickr('.datetimepicker', {
        allowInput: true
      }); // flatpickr
    }
    function oneYear() {
        var date = new Date();
        if (date.getMonth() > 7) {
            date.setFullYear(date.getFullYear() + 1);
        }
        date.setMonth(7);
        date.setDate(31);
        document.getElementById("expiration").value = date.toISOString().split('T')[0];
    }
    function threeYears() {
        var date = new Date();
        if (date.getMonth() > 7) {
            date.setFullYear(date.getFullYear() + 3);
        } else {
            date.setFullYear(date.getFullYear() + 2);
        }
        date.setMonth(7);
        date.setDate(31);
        document.getElementById("expiration").value = date.toISOString().split('T')[0];
    }
</script>
</@template.page>
