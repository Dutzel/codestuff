package haw.aip3.haw.graph;

import haw.aip3.haw.graph.rating.nodes.AuftragsRelation;
import haw.aip3.haw.graph.rating.nodes.GeschaeftspartnerNode;
import haw.aip3.haw.reporting.services.RatingService;
import haw.aip3.rating.graph.repository.AuftragsRelationGraphRepository;
import haw.aip3.rating.graph.repository.GeschaeftspartnerGraphRepository;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=RatingTestConfig.class)
public class AuftragRatingTest {

	@Autowired
	private RatingService ratingService;
	
	@Autowired
	private GeschaeftspartnerGraphRepository geschaeftspartnerGraphRepository;
	
	@Autowired 
    private Neo4jTemplate neo4jTemplate;

	@Autowired
	private AuftragsRelationGraphRepository auftragGraphRepository;
	
    @Test
    public void testRating() {
    	String city = "Hamburg";
    	String product = "Produkt 3";
    	SalesData oldData = null;
    	SalesData newData = null;
    	Iterable<? extends SalesData> salesData = ratingService.showProductSalesByCity(city);
    	for(SalesData data: salesData) {
    		if(product.equals(data.getBauteil().getProduktName())) {
    			oldData = data;
    		}
		}
    	Assert.notNull(oldData);
    	System.out.println(city+"-->"+oldData.getBauteil().getProduktName()+": "+oldData.getCount());
    	
    	// order new product
    	int count = 0;
    	Collection<GeschaeftspartnerNode> kunden = geschaeftspartnerGraphRepository.findByStadt(city);
    	for(GeschaeftspartnerNode k: kunden) {
    		AuftragsRelation r = k.addBestellung(oldData.getBauteil(), 2);
    		count++;
    		auftragGraphRepository.save(r);
    	}
    	
    	salesData = ratingService.showProductSalesByCity(city);
    	for(SalesData data: salesData) {
    		if(product.equals(data.getBauteil().getProduktName())) {
    			newData = data;
    		}
		}
    	System.out.println(city+"--"+newData+"--"+newData.getBauteil()+":"+newData.getCount());
    	Assert.notNull(newData);
    	Assert.isTrue(oldData.getCount()+(count*2) == newData.getCount()); // each added two so we should have count*2 more
    }
}

