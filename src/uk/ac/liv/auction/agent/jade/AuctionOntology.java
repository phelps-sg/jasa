/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 */

package uk.ac.liv.auction.agent.jade;

import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.core.*;

import jade.content.onto.*;
import jade.content.schema.*;

public class AuctionOntology extends Ontology {

  /**
    A symbolic constant, containing the name of this ontology.
   */
  public static final String NAME = "jasa-auction-ontology";

  // VOCABULARY

  // Concepts
  public static final String CONCEPT_SHOUT = "SHOUT";
  public static final String CONCEPT_SHOUT_AGENT = "agent-name";
  public static final String CONCEPT_SHOUT_QUANTITY = "quantity";
  public static final String CONCEPT_SHOUT_PRICE = "price";
  public static final String CONCEPT_SHOUT_IS_BID = "is-bid";
  public static final String CONCEPT_SHOUT_ID = "id";

  // Actions

  public static final String ACTION_REQUEST_SHOUT = "REQUEST-SHOUT";

  public static final String ACTION_NEW_SHOUT = "NEW-SHOUT";
  public static final String ACTION_NEW_SHOUT_SHOUT = "shout";

  public static final String ACTION_REGISTER = "REGISTER";
  public static final String ACTION_REGISTER_AGENT = "agent";

  public static final String ACTION_START_AUCTION = "START-AUCTION";

  public static final String ACTION_REMOVE_SHOUT = "REMOVE-SHOUT";
  public static final String ACTION_REMOVE_SHOUT_SHOUT = "shout";

  // Predicates
  public static final String PREDICATE_BID_SUCCESSFUL = "BID-SUCCESSFUL";
  public static final String PREDICATE_BID_SUCCESSFUL_SHOUT = "shout";
  public static final String PREDICATE_BID_SUCCESSFUL_SELLER = "seller";
  public static final String PREDICATE_BID_SUCCESSFUL_PRICE = "price";
  public static final String PREDICATE_BID_SUCCESSFUL_QUANTITY = "quantity";

  private static Ontology theInstance = new AuctionOntology();

  /**
     This method grants access to the unique instance of the
     ontology.
     @return An <code>Ontology</code> object, containing the concepts
     of the ontology.
  */
   public static Ontology getInstance() {
                return theInstance;
   }

  /**
   * Constructor
   */
  private AuctionOntology() {

    super(NAME, BasicOntology.getInstance());


    try {

      ConceptSchema shoutSchema = new ConceptSchema(CONCEPT_SHOUT);

      shoutSchema.add(CONCEPT_SHOUT_AGENT,
                      (PrimitiveSchema)getSchema(BasicOntology.STRING));

      shoutSchema.add(CONCEPT_SHOUT_QUANTITY,
                      (PrimitiveSchema)getSchema(BasicOntology.INTEGER));

      shoutSchema.add(CONCEPT_SHOUT_PRICE,
                      (PrimitiveSchema)getSchema(BasicOntology.FLOAT));

      shoutSchema.add(CONCEPT_SHOUT_IS_BID,
                      (PrimitiveSchema)getSchema(BasicOntology.BOOLEAN));

      shoutSchema.add(CONCEPT_SHOUT_ID,
                      (PrimitiveSchema)getSchema(BasicOntology.INTEGER));

      add(shoutSchema, ACLShout.class);


      PredicateSchema requestShoutSchema =
          new PredicateSchema(ACTION_REQUEST_SHOUT);

      add(requestShoutSchema, RequestShoutAction.class);


      PredicateSchema newShoutSchema = new PredicateSchema(ACTION_NEW_SHOUT);

      newShoutSchema.add(ACTION_NEW_SHOUT_SHOUT, shoutSchema);

      add(newShoutSchema, NewShoutAction.class);


      PredicateSchema removeShoutSchema = new PredicateSchema(ACTION_REMOVE_SHOUT);

      removeShoutSchema.add(ACTION_REMOVE_SHOUT_SHOUT, shoutSchema);

      add(removeShoutSchema, RemoveShoutAction.class);


      PredicateSchema registerSchema = new PredicateSchema(ACTION_REGISTER);
      registerSchema.add(ACTION_REGISTER_AGENT,
                         (PrimitiveSchema) getSchema(BasicOntology.STRING));
      add(registerSchema, RegisterAction.class);

      PredicateSchema startAuctionSchema =
          new PredicateSchema(ACTION_START_AUCTION);
      add(startAuctionSchema, StartAuctionAction.class);

      PredicateSchema bidSuccessfulSchema =
          new PredicateSchema(PREDICATE_BID_SUCCESSFUL);

      bidSuccessfulSchema.add(PREDICATE_BID_SUCCESSFUL_PRICE,
                              (PrimitiveSchema) getSchema(BasicOntology.FLOAT));

      bidSuccessfulSchema.add(PREDICATE_BID_SUCCESSFUL_QUANTITY,
                              (PrimitiveSchema) getSchema(BasicOntology.INTEGER));

      bidSuccessfulSchema.add(PREDICATE_BID_SUCCESSFUL_SELLER,
                              (PrimitiveSchema) getSchema(BasicOntology.STRING));

      bidSuccessfulSchema.add(PREDICATE_BID_SUCCESSFUL_SHOUT, shoutSchema);

      add(bidSuccessfulSchema, BidSuccessfulPredicate.class);

    } catch(OntologyException oe) {
      oe.printStackTrace();
    }
  }
}

