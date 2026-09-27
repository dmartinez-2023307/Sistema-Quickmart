package org.kinalquickmart.system.service;

import org.kinalquickmart.system.dao.CategoryDAO;
import org.kinalquickmart.system.model.Category;
import java.util.List;

public class CategoryService {
    
    private CategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAO();
    }

    /**
     * Obtiene todas las categorías registradas en la base de datos.
     * @return Lista de objetos Category.
     */
    public List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }
    
    // NOTA: Si en el futuro agregas 'saveCategory' o 'updateCategory' al CategoryDAO,
    // los agregarás aquí con su respectiva validación de negocio.
}