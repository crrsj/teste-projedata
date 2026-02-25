<template>
  <div class="container-fluid py-4 px-5">
    <h1 class="text-center mb-5 text-primary fw-bold">🏭 Materials Management.</h1>

    <div class="row g-4">
      <div class="col-lg-4 col-md-12">
        
        <div class="card mb-4 shadow-sm border-primary">
          <div class="card-header bg-primary text-white fw-bold">New Product</div>
          <div class="card-body">
            <input type="text" class="form-control mb-2" v-model="productForm.name" placeholder="Name (Ex: Table)">
            <input type="number" class="form-control mb-2" v-model="productForm.price" placeholder="Unit Price">
            <button class="btn btn-primary w-100" @click="saveProduct">Create Product</button>
          </div>
        </div>

        <div class="card mb-4 shadow-sm border-secondary">
          <div class="card-header bg-secondary text-white fw-bold">New Raw Material</div>
          <div class="card-body">
            <input type="text" class="form-control mb-2" v-model="materialForm.name" placeholder="Name (Ex: Wood)">
            <input type="number" class="form-control mb-2" v-model="materialForm.stockQuantity" placeholder="Stock Quantity">
            <button @click="saveRawMaterial" class="btn btn-secondary w-100">Create Material</button>
          </div>
        </div>

        <div class="card shadow-sm border-warning">
          <div class="card-header bg-warning fw-bold">Link Composition</div>
          <div class="card-body">
            <label class="small fw-bold">Product:</label>
            <select class="form-select mb-2" v-model="compositionForm.productId">
              <option :value="null">Select  product...</option>
              <option v-for="p in products" :key="p.id" :value="p.id">{{ p.name }}</option>
            </select>
            <label class="small fw-bold">Material:</label>
            <select class="form-select mb-2" v-model="compositionForm.rawMaterialId">
              <option :value="null">Select  material...</option>
              <option v-for="m in materials" :key="m.id" :value="m.id">{{ m.name }}</option>
            </select>
            <label class="small fw-bold">Quantity Required:</label>
            <input type="number" class="form-control mb-2" v-model="compositionForm.quantity">
            <button class="btn btn-warning w-100 text-white fw-bold" @click="saveComposition">Save Composition</button>
          </div>
        </div>
      </div>

      <div class="col-lg-8 col-md-12">
        
        <div class="card shadow-sm mb-4">
          <div class="card-header bg-white fw-bold">Registred Products</div>
          <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
              <thead class="table-light">
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Price</th>
                  <th class="text-center">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="prod in products" :key="prod.id">
                  <td>{{ prod.id }}</td>
                  <td>{{ prod.name }}</td>
                  <td class="text-end">{{ formatCurrency(prod.price) }}</td>
                  <td class="text-center">
                    <div class="d-flex justify-content-center gap-2">
                      <button @click="openProductEditModal(prod.id)" class="btn btn-sm btn-outline-warning">Edit</button>
                      <button @click="deleteProduct(prod.id)" class="btn btn-danger btn-sm">Delete</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div class="card shadow-sm mb-4">
          <div class="card-header bg-white fw-bold">Material Stock</div>
          <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
              <thead class="table-light">
                <tr>
                  <th>Material</th>
                  <th>Stock</th>
                  <th class="text-center">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="mat in materials" :key="mat.id">
                  <td>{{ mat.name }}</td>
                  <td>{{ mat.stockQuantity }}</td>
                  <td class="text-center">
                    <div class="d-flex justify-content-center gap-2">
                      <button @click="openEditModal(mat.id)" class="btn btn-sm btn-outline-warning">Edit</button>
                      <button @click="deleteRawMaterial(mat.id)" class="btn btn-outline-danger btn-sm">Delete</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div class="card shadow-sm border-dark">
          <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center">
            <span class="fw-bold">product Suggestions (What can I manufacture?)</span>
            <button @click="loadSuggestions" class="btn btn-sm btn-light fw-bold">🔄 Calculate</button>
          </div>
          <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
              <thead class="table-light">
                <tr>
                  <th>Product</th>
                  <th>Suggested Quantity</th>
                  <th>Estimated Value</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(item, index) in suggestions" :key="index">                
                  <td class="fw-bold">{{ item.productName }}</td>
                  <td><span class="badge bg-success fs-6">{{ item.quantity }}</span></td>
                 <td class="text-success font-weight-bold text-end">
                   {{ formatCurrency(item.totalValue) }}
                 </td>
                </tr>
                <tr v-if="suggestions.length === 0">
                  <td colspan="3" class="text-center text-muted">
                   No production suggestions available.
                 </td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div v-if="showEditModal" class="modal-backdrop fade show"></div>
  <div v-if="showEditModal" class="modal d-block" tabindex="-1">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Edit Raw Material</h5>
          <button type="button" class="btn-close" @click="showEditModal = false"></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <label>Material Name</label>
            <input v-model="materialToEdit.name" type="text" class="form-control">
          </div>
          <div class="mb-3">
            <label>Stock Quantity</label>
            <input v-model="materialToEdit.stockQuantity" type="number" class="form-control">
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" @click="showEditModal = false">Cancel</button>
          <button type="button" class="btn btn-warning" @click="updateRawMaterial">Save Changes</button>
        </div>
      </div>
    </div>
  </div>

  <div v-if="showProductEditModal" class="modal-backdrop fade show"></div>
  <div v-if="showProductEditModal" class="modal d-block" tabindex="-1">
    <div class="modal-dialog">
      <div class="modal-content border-primary shadow">
        <div class="modal-header bg-primary text-white">
          <h5 class="modal-title">Edit Product</h5>
          <button type="button" class="btn-close" @click="showProductEditModal = false"></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label fw-bold">Product Name</label>
            <input v-model="productToEdit.name" type="text" class="form-control">
          </div>
          <div class="mb-3">
            <label class="form-label fw-bold">Sale Price</label>
            <div class="input-group">
              <span class="input-group-text">R$</span>
              <input v-model="productToEdit.price" type="number" step="0.01" class="form-control">
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" @click="showProductEditModal = false">Cancel</button>
          <button type="button" class="btn btn-primary" @click="updateProduct">Save Changes</button>
        </div>
      </div>
    </div>
  </div>
</template>



<script setup>
import { ref, onMounted } from 'vue';
import api from './services/api';



const products = ref([]);
const materials = ref([]);
const suggestions = ref([]);


const productForm = ref({ name: '', price: null });
const materialForm = ref({ name: '', stockQuantity: null });
const compositionForm = ref({ productId: null, rawMaterialId: null, quantity: null });

const fetchData = async () => {
  try {
    const responseProd = await api.get('/product');
    const responseMat = await api.get('/material');
  
    products.value = responseProd.data;
    materials.value = responseMat.data;
  } catch (error) {
    console.error("Error retrieving data from the API. Check if Java is running!");
  }
};



const loadSuggestions = async () => {
  try {
  
    const response = await api.get('/composition/calculate');
    suggestions.value = response.data; 
  } catch (error) {
    console.error("Error when calculating production:", error);
  }
};


onMounted(() => {
  fetchData();
  loadSuggestions();
});

const saveComposition = async () => {
  const { productId, rawMaterialId, quantity } = compositionForm.value;

 
  if (!productId || !rawMaterialId || !quantity) {
    alert("Please select the Product, Material, and Quantity!");
    return;
  }

  try {
    
    
    await api.post(`/composition/${productId}/${rawMaterialId}`, {
      quantity: parseFloat(quantity)
    });

    alert("Composition saved successfully!");
    
    
    compositionForm.value.quantity = null;
    
    
    loadSuggestions(); 
    
  } catch (error) {
    console.error("Full error:", error.response);
    const mensagemErro = error.response?.data?.message || "Error saving composition.";
    alert(mensagemErro);
  }
};

const saveProduct = async () => {
  if (!productForm.value.name || !productForm.value.price) {
    alert("Please fill in all fields!");
    return;
  }

  try {
    await api.post('/product', {
      name: productForm.value.name,
      price: parseFloat(productForm.value.price)
    });

    alert("Product saved successfully!");
    
   
    productForm.value = { name: '', price: null };
    
    
    fetchData(); 
  } catch (error) {
    console.error("Error saving product:", error);
    alert("Failed to save product.");
  }
};

const saveRawMaterial = async () => {

  if (!materialForm.value.name || materialForm.value.stockQuantity === null) {
    alert("Please fill in the material name and stock quantity!");
    return;
  }

  try {
   
    await api.post('/material', {
      name: materialForm.value.name,
      stockQuantity: parseFloat(materialForm.value.stockQuantity)
    });

    alert("Material saved successfully!");

    
    materialForm.value.name = '';
    materialForm.value.stockQuantity = null;

   
    fetchData(); 
  } catch (error) {
    console.error("Error saving material:", error);
    alert("Error saving material. Check the console for details.");
  }
};

const deleteRawMaterial = async (id) => {
 
  if (confirm("Do you really want to delete this material? This will remove all links associated with it.")) {
    try {
   
      await api.delete(`/material/${id}`);
      
      alert("Material and components successfully removed!");     
      
      fetchData();
      loadSuggestions();
    } catch (error) {
      
      const msg = error.response?.data || "Error deleting material.";
      alert(msg);
    }
  }
};


const showEditModal = ref(false);
const materialToEdit = ref({ id: null, name: '', stockQuantity: null });


const openEditModal = async (id) => {
  try {
    const response = await api.get(`/material/${id}`);
    materialToEdit.value = response.data; 
    showEditModal.value = true;           
  } catch (error) {
    alert("Error fetching material data");
  }
};


const updateRawMaterial = async () => {
  try {
    await api.patch(`/material/${materialToEdit.value.id}`, {
      name: materialToEdit.value.name,
      stockQuantity: materialToEdit.value.stockQuantity
    });
    
    alert("Material updated successfully!");
    showEditModal.value = false; 
    fetchData();                 
    loadSuggestions();           
  } catch (error) {
    alert("Error updating material");
  }
};



const deleteProduct = async (id) => {
  if (confirm("Do you want to delete this product? This will remove its components.")) {
    try {
      await api.delete(`/product/${id}`);
      alert("Product removed!");
      fetchData();
      loadSuggestions();
    } catch (error) {
      alert("Error deleting product!");
    }
  }
};



const showProductEditModal = ref(false);
const productToEdit = ref({ id: null, name: '', price: null });

const openProductEditModal = async (id) => {
 
  showProductEditModal.value = true; 
  
  try {
    console.log("Buscando produto ID:", id);
    const response = await api.get(`/product/${id}`);
    

    productToEdit.value = response.data;
  } catch (error) {
    console.error("The API failed, but the modal opened with empty data.", error);
    Opcional: alert("Error loading data, but the modal opened.");
  }
};



const updateProduct = async () => {
  try {
    await api.patch(`/product/${productToEdit.value.id}`, {
      name: productToEdit.value.name,
      price: productToEdit.value.price
    });
    alert("Product updated successfully!");
    showProductEditModal.value = false;
    fetchData(); 
  } catch (error) {
    alert("Error updating product.");
  }
};

const formatCurrency = (value) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(value);
};


onMounted(() => {
  fetchData(); 
 
});

</script>





<style scoped>

.container-fluid {
  background-color: #f0f2f5;
  min-height: 100vh;
}

/* Títulos */
h1 {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  letter-spacing: -1px;
}

/* Estilo dos Cards */
.card {
  border: none !important;
  border-radius: 12px !important;
  transition: transform 0.2s ease;
}

.card-header {
  border-bottom: none;
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* Tabelas */
.table-responsive {
  border-radius: 0 0 12px 12px;
}

.table thead th {
  font-size: 0.8rem;
  text-transform: uppercase;
  color: #6c757d;
  font-weight: 700;
  border-top: none;
}

/* Ajuste fino dos botões para ficarem sempre em linha */
.gap-2 {
  gap: 0.5rem !important;
}

.btn-sm {
  padding: 0.25rem 0.75rem;
  font-weight: 600;
}

/* Inputs */
.form-control:focus, .form-select:focus {
  border-color: #0d6efd;
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.15);
}

/* Efeito Hover nos cards da coluna da esquerda */
.col-lg-4 .card:hover {
  transform: translateY(-3px);
}
</style>